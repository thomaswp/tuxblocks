//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: C:\Users\Thomas\Documents\Eclipse\Tux\tuxblocks\core\src\main\java\tuxkids\tuxblocks\core\defense\round\Level.java
//
//  Created by Thomas on 7/1/13.
//

@class TBWalker;
@protocol JavaUtilList;

#import "JreEmulation.h"
#import "tuxkids/tuxblocks/core/defense/round/Round.h"

@protocol TBLevel_RoundCompletedListener < NSObject >
- (void)onRoundCompletedWithTBRound:(TBRound *)round;
@end

@interface TBLevel : NSObject {
 @public
  id<JavaUtilList> rounds_;
  id<JavaUtilList> waitTimes_;
  id<TBLevel_RoundCompletedListener> roundCompletedListener_;
  int timer_;
  TBRound *currentRound_;
  int round__;
}

@property (nonatomic, retain) id<JavaUtilList> rounds;
@property (nonatomic, retain) id<JavaUtilList> waitTimes;
@property (nonatomic, retain) id<TBLevel_RoundCompletedListener> roundCompletedListener;
@property (nonatomic, assign) int timer;
@property (nonatomic, retain) TBRound *currentRound;
@property (nonatomic, assign) int round_;

- (int)round;
- (int)timeUntilNextRound;
- (void)startNextRound;
- (BOOL)duringRound;
- (void)setRoundCompletedListenerWithTBLevel_RoundCompletedListener:(id<TBLevel_RoundCompletedListener>)roundCompletedListener;
- (void)populateLevel;
- (id)init;
- (void)addRoundWithTBRound:(TBRound *)round
                  withFloat:(float)waitTimeSeconds;
- (TBWalker *)updateWithInt:(int)delta;
- (BOOL)finished;
+ (TBLevel *)generate;
@end

typedef TBLevel TuxkidsTuxblocksCoreDefenseRoundLevel;

@interface TBLevel_$1 : TBLevel {
 @public
  TBWalker *val$basic_;
  TBWalker *val$medium_;
  TBWalker *val$quick_;
  TBWalker *val$hard_;
}

@property (nonatomic, retain) TBWalker *val$basic;
@property (nonatomic, retain) TBWalker *val$medium;
@property (nonatomic, retain) TBWalker *val$quick;
@property (nonatomic, retain) TBWalker *val$hard;

- (void)populateLevel;
- (id)initWithTBWalker:(TBWalker *)capture$0
          withTBWalker:(TBWalker *)capture$1
          withTBWalker:(TBWalker *)capture$2
          withTBWalker:(TBWalker *)capture$3;
@end

@interface TBLevel_$1_$1 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$2 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$3 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$4 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$5 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$6 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$7 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$8 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$9 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end

@interface TBLevel_$1_$10 : TBRound {
 @public
  TBLevel_$1 *this$0_;
}

@property (nonatomic, retain) TBLevel_$1 *this$0;

- (void)populateRound;
- (id)initWithTBLevel_$1:(TBLevel_$1 *)outer$;
@end