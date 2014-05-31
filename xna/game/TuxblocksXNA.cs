using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using playn.core;
using PlayNXNA;

namespace Tuxblocks
{
    public class TuxblocksXNA : XNAGame
    {
        protected override void Initialize()
        {
            base.Initialize();

            Importer.Import();
            Game game = new tuxkids.tuxblocks.core.TuxBlocksGame("en");
            PlayN.run(game);
        }

        protected override XNAPlatform registerPlatform()
        {
            return XNAPlatform.register();
        }

        static void Main(string[] args)
        {
            using (TuxblocksXNA game = new TuxblocksXNA())
            {
                game.Run();
            }
        }
    }
}
