package me.ivanyf.authdomain;

public class DomainPattern {

    public static DomainPattern compile(String domainPattern) {
        return new DomainPattern(domainPattern);
    }

    private final String[] pattern;

    public DomainPattern(String domainPattern) {
        this.pattern = parse(domainPattern);
    }

    public boolean matches(String domainInput) {
        String[] inputs = parse(domainInput);
        if (inputs.length != pattern.length) return false;
        for (int i = 0; i < inputs.length; i++) {
            String part = pattern[i];
            if (part.equals("*")) continue;
            if (!part.equalsIgnoreCase(inputs[i])) {
                return false;
            }
        }
        return true;
    }

    private static String[] parse(String pattern) {
        return normalize(pattern).split("\\.");
    }

    private static String normalize(String domain) {
        if (domain == null || domain.isEmpty()) return "";
        int start = 0;
        int end = domain.length();
        while (start < end && domain.charAt(start) == '.') start++;
        while (end > start && domain.charAt(end - 1) == '.') end--;
        return start > 0 || end < domain.length() ? domain.substring(start, end) : domain;
    }

}