package utils;

//Supported languages: KZ, EN, RU
  
public enum Language {
    KZ("Қазақша"),
    EN("English"),
    RU("Русский");

    private final String displayName;

    Language(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }

    public static Language fromCode(String code) {
        return switch (code.toUpperCase()) {
            case "KZ" -> KZ;
            case "RU" -> RU;
            default   -> EN;
        };
    }
}
