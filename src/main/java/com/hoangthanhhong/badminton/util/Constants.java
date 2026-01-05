package com.hoangthanhhong.badminton.util;

public class Constants {

    // Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_STAFF = "STAFF";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = { "image/jpeg", "image/png", "image/gif" };

    // Booking
    public static final int MIN_BOOKING_DURATION = 60; // minutes
    public static final int MAX_BOOKING_DURATION = 180; // minutes

    // Loyalty Points
    public static final int POINTS_PER_VND = 1; // 1 point per 1000 VND
    public static final int POINTS_EXPIRY_DAYS = 365;
}
