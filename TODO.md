TODO
====

Suggestions from the Beta
-------------------------
These were suggestions made by players during the beta:
* Create a button to flip the sign of the selected number on the NumberSelectScreen (possibly replacing the center button).
* Rework the Tutorial to be more flexible, so that it depends on conditions being met, rather than specific actions happening in a specified order. For instance, rather than requiring the player to zoom in on the Grid, select a Tower and upgrade it, simply require that at some point the player upgrade a tower, and skip/modify instructions if that has already occurred.
* Include stronger checks in the EquationGenerator class to ensure terms do not get too large (over 1000 or so). This is mainly a problem with the composite function generator, which does not have term-limiting functionality at present.
* When a problem is solved, perhaps the SolveScreen should simply exit, rather than requiring the player to hit the check button.
* Perhaps use a different font or text color when drawing operators on modifier blocks, specifically to distinguish the variable x from the times symbol.
* Create an "advanced" mode where the player must solve equations on paper and input just the answers. (The developer holds some serious reservations about this idea.)
* Find a way to allow players to simplify multiple ModifierBlocks as once. For instance 1 + 2 + 3 => 6, rather than 1 + 2 => 3 and 3 + 3 => 6.
* Highlight the Repeat and Cancel buttons when the Tutorial mentions them.
* The tutorial level should start out with part of a maze of Towers already built, to demonstrate the technique.
* Add the ability to change a tower's placement immediately after it's been placed, in case the player makes a mistake.
* Make enemies faster (and perhaps weaker to balance things) so the rounds go quicker, especially since players can't do anything during them.
* Look into more interesting art for Towers and Walkers.
* Add a visible Menu (or at least mute) button to the TitleScreen and DefenseScreen
* Buttons should not highlight when you mouse over them if the menu is open (unless the buttons are on the menu)

Possible Issues
---------------------
The following are issues reported in the Beta, but not confirmed. It would be wise to watch out for them:
* Unsolvable problems can be generated
* Walkers that aren't supposed to be able to fly sometimes pass through Towers
* Sound and highlighting (in the tutorial) are not working for some browsers (reported on Firefox 17.0.5)

Work for the Future
-------------------
These are the developer's goals for the future:
* Create more types of grids, as opposed to the simple one entrance/on exit model currently being used. For instance, there might be an entrance for Walkers on the top and left sides of the grid, and corresponding exits on the bottom and right sides.
* Allow the player to have more advanced options, like selecting the theme color, the type of grid (see above) or the number of rounds/levels in the game.
* Create a high scores screen.
* After an equation is generated, shuffle the expressions around so that all expressions of a given form don't look the same.
* There are a number of TODO's in the code, many of which really should be addressed for readability, stability or efficiency. Some should probably be dismissed and deleted.
* Add documentation of how to add additional languages
