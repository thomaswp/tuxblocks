using PlayNXNA;
using playn.core;

namespace Tuxblocks
{
    public class Importer
    {
        public static void Import()
        {
            XNAGraphics graphics = (XNAGraphics)PlayN.graphics();
            graphics.registerFont("Arial", "fonts\\Arial-8.0-Plain", 8.0f, Font.Style.PLAIN);
            graphics.registerFont("Arial", "fonts\\Arial-8.0-Italic", 8.0f, Font.Style.ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-8.0-Bold", 8.0f, Font.Style.BOLD);
            graphics.registerFont("Arial", "fonts\\Arial-8.0-BoldItalic", 8.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-12.0-Plain", 12.0f, Font.Style.PLAIN);
            graphics.registerFont("Arial", "fonts\\Arial-12.0-Italic", 12.0f, Font.Style.ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-12.0-Bold", 12.0f, Font.Style.BOLD);
            graphics.registerFont("Arial", "fonts\\Arial-12.0-BoldItalic", 12.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-16.0-Plain", 16.0f, Font.Style.PLAIN);
            graphics.registerFont("Arial", "fonts\\Arial-16.0-Italic", 16.0f, Font.Style.ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-16.0-Bold", 16.0f, Font.Style.BOLD);
            graphics.registerFont("Arial", "fonts\\Arial-16.0-BoldItalic", 16.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-24.0-Plain", 24.0f, Font.Style.PLAIN);
            graphics.registerFont("Arial", "fonts\\Arial-24.0-Italic", 24.0f, Font.Style.ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-24.0-Bold", 24.0f, Font.Style.BOLD);
            graphics.registerFont("Arial", "fonts\\Arial-24.0-BoldItalic", 24.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-36.0-Plain", 36.0f, Font.Style.PLAIN);
            graphics.registerFont("Arial", "fonts\\Arial-36.0-Italic", 36.0f, Font.Style.ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-36.0-Bold", 36.0f, Font.Style.BOLD);
            graphics.registerFont("Arial", "fonts\\Arial-36.0-BoldItalic", 36.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-48.0-Plain", 48.0f, Font.Style.PLAIN);
            graphics.registerFont("Arial", "fonts\\Arial-48.0-Italic", 48.0f, Font.Style.ITALIC);
            graphics.registerFont("Arial", "fonts\\Arial-48.0-Bold", 48.0f, Font.Style.BOLD);
            graphics.registerFont("Arial", "fonts\\Arial-48.0-BoldItalic", 48.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-8.0-Plain", 8.0f, Font.Style.PLAIN);
            graphics.registerFont("Raavi", "fonts\\Raavi-8.0-Italic", 8.0f, Font.Style.ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-8.0-Bold", 8.0f, Font.Style.BOLD);
            graphics.registerFont("Raavi", "fonts\\Raavi-8.0-BoldItalic", 8.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-12.0-Plain", 12.0f, Font.Style.PLAIN);
            graphics.registerFont("Raavi", "fonts\\Raavi-12.0-Italic", 12.0f, Font.Style.ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-12.0-Bold", 12.0f, Font.Style.BOLD);
            graphics.registerFont("Raavi", "fonts\\Raavi-12.0-BoldItalic", 12.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-16.0-Plain", 16.0f, Font.Style.PLAIN);
            graphics.registerFont("Raavi", "fonts\\Raavi-16.0-Italic", 16.0f, Font.Style.ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-16.0-Bold", 16.0f, Font.Style.BOLD);
            graphics.registerFont("Raavi", "fonts\\Raavi-16.0-BoldItalic", 16.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-24.0-Plain", 24.0f, Font.Style.PLAIN);
            graphics.registerFont("Raavi", "fonts\\Raavi-24.0-Italic", 24.0f, Font.Style.ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-24.0-Bold", 24.0f, Font.Style.BOLD);
            graphics.registerFont("Raavi", "fonts\\Raavi-24.0-BoldItalic", 24.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-36.0-Plain", 36.0f, Font.Style.PLAIN);
            graphics.registerFont("Raavi", "fonts\\Raavi-36.0-Italic", 36.0f, Font.Style.ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-36.0-Bold", 36.0f, Font.Style.BOLD);
            graphics.registerFont("Raavi", "fonts\\Raavi-36.0-BoldItalic", 36.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-48.0-Plain", 48.0f, Font.Style.PLAIN);
            graphics.registerFont("Raavi", "fonts\\Raavi-48.0-Italic", 48.0f, Font.Style.ITALIC);
            graphics.registerFont("Raavi", "fonts\\Raavi-48.0-Bold", 48.0f, Font.Style.BOLD);
            graphics.registerFont("Raavi", "fonts\\Raavi-48.0-BoldItalic", 48.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-8.0-Plain", 8.0f, Font.Style.PLAIN);
            graphics.registerFont("Mangal", "fonts\\Mangal-8.0-Italic", 8.0f, Font.Style.ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-8.0-Bold", 8.0f, Font.Style.BOLD);
            graphics.registerFont("Mangal", "fonts\\Mangal-8.0-BoldItalic", 8.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-12.0-Plain", 12.0f, Font.Style.PLAIN);
            graphics.registerFont("Mangal", "fonts\\Mangal-12.0-Italic", 12.0f, Font.Style.ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-12.0-Bold", 12.0f, Font.Style.BOLD);
            graphics.registerFont("Mangal", "fonts\\Mangal-12.0-BoldItalic", 12.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-16.0-Plain", 16.0f, Font.Style.PLAIN);
            graphics.registerFont("Mangal", "fonts\\Mangal-16.0-Italic", 16.0f, Font.Style.ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-16.0-Bold", 16.0f, Font.Style.BOLD);
            graphics.registerFont("Mangal", "fonts\\Mangal-16.0-BoldItalic", 16.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-24.0-Plain", 24.0f, Font.Style.PLAIN);
            graphics.registerFont("Mangal", "fonts\\Mangal-24.0-Italic", 24.0f, Font.Style.ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-24.0-Bold", 24.0f, Font.Style.BOLD);
            graphics.registerFont("Mangal", "fonts\\Mangal-24.0-BoldItalic", 24.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-36.0-Plain", 36.0f, Font.Style.PLAIN);
            graphics.registerFont("Mangal", "fonts\\Mangal-36.0-Italic", 36.0f, Font.Style.ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-36.0-Bold", 36.0f, Font.Style.BOLD);
            graphics.registerFont("Mangal", "fonts\\Mangal-36.0-BoldItalic", 36.0f, Font.Style.BOLD_ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-48.0-Plain", 48.0f, Font.Style.PLAIN);
            graphics.registerFont("Mangal", "fonts\\Mangal-48.0-Italic", 48.0f, Font.Style.ITALIC);
            graphics.registerFont("Mangal", "fonts\\Mangal-48.0-Bold", 48.0f, Font.Style.BOLD);
            graphics.registerFont("Mangal", "fonts\\Mangal-48.0-BoldItalic", 48.0f, Font.Style.BOLD_ITALIC);
        }
    }
}
