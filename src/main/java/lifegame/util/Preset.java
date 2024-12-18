package lifegame.util;

import java.io.File;
import java.util.List;

public class Preset {
    private static final String PRESETS_DIR = "src/main/resources/presets";

    private final String displayName;
    private final String fileName;
    private Preset(String displayName, String fileName) {
        this.displayName = displayName;
        this.fileName = fileName;
    }
    public String getDisplayName() {
        return displayName;
    }
    public File getFile() {
        return new File(PRESETS_DIR+"/"+fileName);
    }

    public static final List<Preset> PRESETS = List.of(
        new Preset("グライダー", "glider.lg"),
        new Preset("グライダーガン", "glider-gun.lg"),
        new Preset("シャトル", "shuttle.lg"),
        new Preset("どんぐり", "acorn.lg"),
        new Preset("大阪大学", "ou.lg")
    );
}
