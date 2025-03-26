package cellcorp.gameofcells.objects.Size;

public class MassiveSizeUpgrade extends SizeUpgrade {
    public MassiveSizeUpgrade() {
        super(1, 100, 3, "Massive");
    }

    @Override
    public String getPerks() {
        return "• +1 Size Unit\n• Maximum growth\n• " + getVisualEffect();
    }

    @Override
    public String getVisualEffect() {
        return "Glowing nucleus";
    }
}
