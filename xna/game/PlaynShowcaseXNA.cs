using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using playn.core;
using PlayNXNA;

namespace PlaynShowcase
{
    public class PlaynShowcaseXNA : XNAGame, playn.showcase.core.Showcase.DeviceService
    {
        protected override void Initialize()
        {
            base.Initialize();

            Importer.Import();
            Game game = new playn.showcase.core.Showcase(this);
            PlayN.run(game);
        }

        protected override XNAPlatform registerPlatform()
        {
            return XNAPlatform.register();
        }

        static void Main(string[] args)
        {
            using (PlaynShowcaseXNA game = new PlaynShowcaseXNA())
            {
                game.Run();
            }
        }

        public string info()
        {
            return "XNA Platform";
        }
    }
}
