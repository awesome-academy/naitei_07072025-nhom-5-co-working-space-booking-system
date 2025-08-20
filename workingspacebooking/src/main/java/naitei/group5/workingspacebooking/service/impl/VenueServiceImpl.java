package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.exception.ResourceNotFoundException;
import naitei.group5.workingspacebooking.exception.custom.*;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.repository.VenueStyleRepository;
import naitei.group5.workingspacebooking.service.VenueService;
import naitei.group5.workingspacebooking.specification.VenueSpecs;
import naitei.group5.workingspacebooking.utils.ConverterDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueStyleRepository venueStyleRepository;
    private final UserRepository userRepository;

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
        // 1) Kiểm tra owner hợp lệ & đúng role
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));

        if (!UserRole.owner.equals(owner.getRole())) {
            throw new IllegalArgumentException("User does not have owner permission");
        }

        // 2) Kiểm tra venue style
        VenueStyle venueStyle = venueStyleRepository.findById(requestDto.venueStyleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venue style not found with ID: " + requestDto.venueStyleId()));

        // 3) Map DTO -> Entity
        Venue venue = ConverterDto.toVenueEntity(requestDto, owner, venueStyle);

        // 4) Lưu & map Entity -> Response DTO
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
}
