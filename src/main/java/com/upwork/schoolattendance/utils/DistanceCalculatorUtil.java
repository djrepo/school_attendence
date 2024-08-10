package com.upwork.schoolattendance.utils;

public class DistanceCalculatorUtil {

        private static final int EARTH_RADIUS = 6371; // Radius of the earth in kilometers

        public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            double distanceInKilometers = EARTH_RADIUS * c;
            return distanceInKilometers * 1000; // Convert to meters
        }
}
