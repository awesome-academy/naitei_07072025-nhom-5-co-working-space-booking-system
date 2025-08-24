package naitei.group5.workingspacebooking.constant;

public class Endpoint {
    public static final String BASE = "http://localhost:8080";
    public static final String AUTH_LOGIN = "/api/auth/login";
    public static final String AUTH_REFRESH  = "/api/auth/refresh";
    public static final String AUTH_LOGOUT  = "/api/auth/logout";
    public static final String VNPAY_RETURN  = "/api/payment/vnpay-return";

    public static final String AUTH_REGISTER_RENTER = "/api/auth/register-renter";
    public static final String AUTH_REGISTER_OWNER  = "/api/auth/register-owner";

    public static final String USER_ROLE = "/api/users/**";

    public static final String OWNER_VENUES = "/api/owner/venues/**";

    // từ master
    public static final String AUTH_RECOVERY  = "/api/auth/recover/**";
    public static final String AUTH_RECOVERY_CONFIRM = "/api/auth/recover/confirm?token=";

    // renter - venue
    public static final String RENTER_VENUES = "/api/venues";
    public static final String RENTER_VENUES_SUB = "/api/venues/**";
    public static final String RENTER_VENUES_FILTER = "/api/venues/filter";
    // renter - bookings
    public static final String RENTER_BOOKINGS = "/api/renter/bookings";
    public static final String RENTER_BOOKINGS_HISTORY = "/api/renter/bookings/history";

    // renter - payment
    public static final String RENTER_PAYMENT = "/api/payment/**";
    //owner-bookings
    public static final String OWNER_BOOKINGS = "/api/owner/bookings";
    //admin
    public static final String ADMIN_BASE = "/admin/**";
}
