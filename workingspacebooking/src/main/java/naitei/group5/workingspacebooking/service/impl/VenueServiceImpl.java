package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRenterRequestDto;
import naitei.group5.workingspacebooking.specification.RenterVenueSpecs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.TimeSlotDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.entity.BookingDetail;
import naitei.group5.workingspacebooking.exception.ResourceNotFoundException;
import naitei.group5.workingspacebooking.exception.custom.*;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.repository.VenueStyleRepository;
import naitei.group5.workingspacebooking.repository.BookingDetailRepository;
import naitei.group5.workingspacebooking.service.VenueService;
import naitei.group5.workingspacebooking.service.common.TimeSlotCalculator;
import naitei.group5.workingspacebooking.specification.VenueSpecs;
import naitei.group5.workingspacebooking.utils.ConverterDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueStyleRepository venueStyleRepository;
    private final UserRepository userRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final TimeSlotCalculator timeSlotCalculator;

    // ==== Owner use cases ====
    @Override
    public List<Venue> getVenuesByOwner(Integer ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException();
        }
        return venueRepository.findByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public VenueResponseDto createVenueRequest(Integer ownerId, CreateVenueRequestDto requestDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));

        if (!UserRole.owner.equals(owner.getRole())) {
            throw new IllegalArgumentException("User does not have owner permission");
        }

        VenueStyle venueStyle = venueStyleRepository.findById(requestDto.venueStyleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venue style not found with ID: " + requestDto.venueStyleId()));

        Venue venue = ConverterDto.toVenueEntity(requestDto, owner, venueStyle);
        Venue savedVenue = venueRepository.save(venue);
        return ConverterDto.toVenueResponseDto(savedVenue);
    }

    @Override
    public List<VenueResponseDto> filterOwnerVenues(FilterVenueRequestDto req) {
        if (req.ownerId() == null) {
            throw new OwnerIdRequiredException();
        }
        if (!userRepository.existsById(req.ownerId())) {
            throw new UserNotFoundException();
        }
        if (req.capacityMin() != null && req.capacityMax() != null
                && req.capacityMin() > req.capacityMax()) {
            throw new InvalidCapacityRangeException();
        }

        var spec = VenueSpecs.byFilter(
                req.ownerId(),
                req.name(),
                req.location(),
                req.venueStyleId(),
                req.venueStyleName(),
                req.capacityMin(),
                req.capacityMax(),
                req.verified()
        );

        List<Venue> venues = venueRepository.findAll(spec);
        return venues.stream()
                .map(ConverterDto::toVenueResponseDto)
                .toList();
    }

    // ==== Renter use cases ====
    @Override
    public List<VenueResponseDto> getVerifiedVenues() {
        return venueRepository.findByVerified(true)
                .stream()
                .map(ConverterDto::toVenueResponseDto)
                .toList();
    }

    // Owner xem chi tiết venue (bao gồm busy slots & available slots)
    @Override
    public VenueDetailResponseDto getVenueDetailByOwner(Integer ownerId, Integer venueId) {
        var venue = venueRepository.findByIdAndOwnerIdWithAllDetails(venueId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venue not found with ID: " + venueId + " for owner: " + ownerId));

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end   = start.plusDays(7);

        List<TimeSlotDto> busy = bookingDetailRepository
                .findBusySlotsByVenueAndTimeRange(venueId, start, end)
                .stream()
                .map(bd -> new TimeSlotDto(bd.getStartTime(), bd.getEndTime()))
                .toList();

        var mergedBusy     = timeSlotCalculator.mergeBusy(start, end, busy);
        var availableSlots = timeSlotCalculator.calcAvailable(start, end, mergedBusy);

        return ConverterDto.toVenueDetailResponseDto(venue, availableSlots, mergedBusy);
    }

    // Renter filter venue theo thời gian, tuần, sức chứa
    @Override
    public List<VenueResponseDto> filterVenuesForRenter(FilterVenueRenterRequestDto req) {
        if (req.capacityMin() != null && req.capacityMax() != null
                && req.capacityMin() > req.capacityMax()) {
            throw new InvalidCapacityRangeException();
        }

        if (req.startTime() != null && req.endTime() != null
                && req.startTime().isAfter(req.endTime())) {
            throw new InvalidTimeRangeException();
        }

        var spec = RenterVenueSpecs.byFilter(
                req.name(),
                req.location(),
                req.venueStyleName(),
                req.venueStyleId(),
                req.capacityMin(),
                req.capacityMax(),
                req.startTime(),
                req.endTime(),
                req.weekDays()
        );

        List<Venue> venues = venueRepository.findAll(spec);

        return venues.stream()
                .map(v -> ConverterDto.toVenueResponseDto(
                        v,
                        req.startTime(),
                        req.endTime(),
                        req.weekDays()
                ))
                .toList();
    }
}
