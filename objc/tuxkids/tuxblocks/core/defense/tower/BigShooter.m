//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: C:\Users\Thomas\Documents\Eclipse\Tux\tuxblocks\core\src\main\java\tuxkids\tuxblocks\core\defense\tower\BigShooter.java
//
//  Created by Thomas on 7/1/13.
//

#import "tuxkids/tuxblocks/core/defense/projectile/Missile.h"
#import "tuxkids/tuxblocks/core/defense/projectile/Projectile.h"
#import "tuxkids/tuxblocks/core/defense/tower/BigShooter.h"
#import "tuxkids/tuxblocks/core/defense/tower/Tower.h"

@implementation TBBigShooter

- (int)rows {
  return 2;
}

- (int)cols {
  return 2;
}

- (float)damage {
  return 5;
}

- (int)fireRate {
  return 1500;
}

- (float)range {
  return 6;
}

- (TBProjectile *)createProjectile {
  return [[[TBMissile alloc] init] autorelease];
}

- (TBTower *)copy__ OBJC_METHOD_FAMILY_NONE {
  return [[[TBBigShooter alloc] init] autorelease];
}

- (NSString *)name {
  return @"B.I.G. Shooter";
}

- (int)cost {
  return 4;
}

- (int)commonness {
  return 3;
}

- (float)splashRadius {
  return 1.4f;
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end