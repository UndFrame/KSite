package ru.istu.b1978201.KSite.utils;

public class ServicesId {

    public static long MA_OVER_DIFF = 0;
    public static long MA_NEEDLE = 1;

    public static boolean isSupport(String serviceId) {
        return serviceId.equals(Long.toString(MA_OVER_DIFF)) ||
                serviceId.equals(Long.toString(MA_NEEDLE));
    }
}
