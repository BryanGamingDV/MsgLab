package me.bryangaming.chatlab.dependency;

public class Relocation {
    private final String pattern;
    private final String relocatedPattern;

    private Relocation(String pattern, String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }

    public String getPattern() {
        return pattern;
    }

    public String getRelocatedPattern() {
        return relocatedPattern;
    }

    public static Relocation of(String pattern, String relocatedPattern) {
        return new Relocation(pattern, relocatedPattern);
    }
}