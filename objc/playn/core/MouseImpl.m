//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: playn/core/MouseImpl.java
//
//  Created by Thomas on 7/1/13.
//

#import "AbstractLayer.h"
#import "Dispatcher.h"
#import "Events.h"
#import "Graphics.h"
#import "GroupLayer.h"
#import "Layer.h"
#import "Mouse.h"
#import "PlayN.h"
#import "Point.h"
#import "Transform.h"
#import "MouseImpl.h"

@implementation PlaynCoreMouseImpl

@synthesize enabled = enabled_;
- (id<PlaynCoreDispatcher>)dispatcher {
  return dispatcher_;
}
- (void)setDispatcher:(id<PlaynCoreDispatcher>)dispatcher {
  JreOperatorRetainedAssign(&dispatcher_, self, dispatcher);
}
@synthesize dispatcher = dispatcher_;
- (id<PlaynCoreMouse_Listener>)listener_ {
  return listener__;
}
- (void)setListener_:(id<PlaynCoreMouse_Listener>)listener_ {
  JreOperatorRetainedAssign(&listener__, self, listener_);
}
@synthesize listener_ = listener__;
- (PlaynCoreAbstractLayer *)activeLayer {
  return activeLayer_;
}
- (void)setActiveLayer:(PlaynCoreAbstractLayer *)activeLayer {
  JreOperatorRetainedAssign(&activeLayer_, self, activeLayer);
}
@synthesize activeLayer = activeLayer_;
- (PlaynCoreAbstractLayer *)hoverLayer {
  return hoverLayer_;
}
- (void)setHoverLayer:(PlaynCoreAbstractLayer *)hoverLayer {
  JreOperatorRetainedAssign(&hoverLayer_, self, hoverLayer);
}
@synthesize hoverLayer = hoverLayer_;
- (id<PlaynCoreAbstractLayer_Interaction>)DOWN {
  return DOWN_;
}
- (void)setDOWN:(id<PlaynCoreAbstractLayer_Interaction>)DOWN {
  JreOperatorRetainedAssign(&DOWN_, self, DOWN);
}
@synthesize DOWN = DOWN_;
- (id<PlaynCoreAbstractLayer_Interaction>)UP {
  return UP_;
}
- (void)setUP:(id<PlaynCoreAbstractLayer_Interaction>)UP {
  JreOperatorRetainedAssign(&UP_, self, UP);
}
@synthesize UP = UP_;
- (id<PlaynCoreAbstractLayer_Interaction>)DRAG {
  return DRAG_;
}
- (void)setDRAG:(id<PlaynCoreAbstractLayer_Interaction>)DRAG {
  JreOperatorRetainedAssign(&DRAG_, self, DRAG);
}
@synthesize DRAG = DRAG_;
- (id<PlaynCoreAbstractLayer_Interaction>)MOVE {
  return MOVE_;
}
- (void)setMOVE:(id<PlaynCoreAbstractLayer_Interaction>)MOVE {
  JreOperatorRetainedAssign(&MOVE_, self, MOVE);
}
@synthesize MOVE = MOVE_;
- (id<PlaynCoreAbstractLayer_Interaction>)OVER {
  return OVER_;
}
- (void)setOVER:(id<PlaynCoreAbstractLayer_Interaction>)OVER {
  JreOperatorRetainedAssign(&OVER_, self, OVER);
}
@synthesize OVER = OVER_;
- (id<PlaynCoreAbstractLayer_Interaction>)OUT {
  return OUT_;
}
- (void)setOUT:(id<PlaynCoreAbstractLayer_Interaction>)OUT {
  JreOperatorRetainedAssign(&OUT_, self, OUT);
}
@synthesize OUT = OUT_;
- (id<PlaynCoreAbstractLayer_Interaction>)WHEEL_SCROLL {
  return WHEEL_SCROLL_;
}
- (void)setWHEEL_SCROLL:(id<PlaynCoreAbstractLayer_Interaction>)WHEEL_SCROLL {
  JreOperatorRetainedAssign(&WHEEL_SCROLL_, self, WHEEL_SCROLL);
}
@synthesize WHEEL_SCROLL = WHEEL_SCROLL_;

- (BOOL)hasMouse {
  return YES;
}

- (BOOL)isEnabled {
  return enabled_;
}

- (void)setEnabledWithBOOL:(BOOL)enabled {
  self.enabled = enabled;
}

- (id<PlaynCoreMouse_Listener>)listener {
  return listener__;
}

- (void)setListenerWithPlaynCoreMouse_Listener:(id<PlaynCoreMouse_Listener>)listener {
  self.listener_ = listener;
}

- (void)lock {
}

- (void)unlock {
}

- (BOOL)isLocked {
  return NO;
}

- (BOOL)isLockSupported {
  return NO;
}

- (void)setPropagateEventsWithBOOL:(BOOL)propagate {
  JreOperatorRetainedAssign(&dispatcher_, self, [PlaynCoreDispatcher_Util selectWithBOOL:propagate]);
}

- (BOOL)onMouseDownWithPlaynCoreMouse_ButtonEvent_Impl:(PlaynCoreMouse_ButtonEvent_Impl *)event {
  if (!enabled_) return NO;
  [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_ButtonEvent_Impl *) NIL_CHK(event)) flags]) setPreventDefaultWithBOOL:NO];
  if (listener__ != nil) {
    [listener__ onMouseDownWithPlaynCoreMouse_ButtonEvent:event];
  }
  id<PlaynCoreGroupLayer> root = [((id<PlaynCoreGraphics>) [PlaynCorePlayN graphics]) rootLayer];
  if ([((id<PlaynCoreGroupLayer>) NIL_CHK(root)) interactive]) {
    PythagorasFPoint *p = [[[PythagorasFPoint alloc] initWithFloat:[((PlaynCoreMouse_ButtonEvent_Impl *) NIL_CHK(event)) x] withFloat:[((PlaynCoreMouse_ButtonEvent_Impl *) NIL_CHK(event)) y]] autorelease];
    (void) [((id<PythagorasFTransform>) [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) transform]) inverseTransformWithPythagorasFIPoint:p withPythagorasFPoint:p];
    ((PythagorasFPoint *) NIL_CHK(p)).x_ += [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) originX];
    ((PythagorasFPoint *) NIL_CHK(p)).y_ += [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) originY];
    JreOperatorRetainedAssign(&activeLayer_, self, (PlaynCoreAbstractLayer *) [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) hitTestWithPythagorasFPoint:p]);
    if (activeLayer_ != nil) {
      [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:activeLayer_ withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:DOWN_];
    }
  }
  return [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_ButtonEvent_Impl *) NIL_CHK(event)) flags]) getPreventDefault];
}

- (BOOL)onMouseMoveWithPlaynCoreMouse_MotionEvent_Impl:(PlaynCoreMouse_MotionEvent_Impl *)event {
  if (!enabled_) return NO;
  [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_MotionEvent_Impl *) NIL_CHK(event)) flags]) setPreventDefaultWithBOOL:NO];
  if (listener__ != nil) {
    [listener__ onMouseMoveWithPlaynCoreMouse_MotionEvent:event];
  }
  id<PlaynCoreGroupLayer> root = [((id<PlaynCoreGraphics>) [PlaynCorePlayN graphics]) rootLayer];
  if ([((id<PlaynCoreGroupLayer>) NIL_CHK(root)) interactive]) {
    PythagorasFPoint *p = [[[PythagorasFPoint alloc] initWithFloat:[((PlaynCoreMouse_MotionEvent_Impl *) NIL_CHK(event)) x] withFloat:[((PlaynCoreMouse_MotionEvent_Impl *) NIL_CHK(event)) y]] autorelease];
    (void) [((id<PythagorasFTransform>) [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) transform]) inverseTransformWithPythagorasFIPoint:p withPythagorasFPoint:p];
    ((PythagorasFPoint *) NIL_CHK(p)).x_ += [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) originX];
    ((PythagorasFPoint *) NIL_CHK(p)).y_ += [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) originY];
    PlaynCoreAbstractLayer *lastHoverLayer = hoverLayer_;
    JreOperatorRetainedAssign(&hoverLayer_, self, (PlaynCoreAbstractLayer *) [((id<PlaynCoreGroupLayer>) NIL_CHK(root)) hitTestWithPythagorasFPoint:p]);
    if (activeLayer_ != nil) {
      [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:activeLayer_ withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:DRAG_];
    }
    else if (hoverLayer_ != nil) {
      [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:hoverLayer_ withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:MOVE_];
    }
    if (lastHoverLayer != hoverLayer_ && lastHoverLayer != nil) {
      [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:lastHoverLayer withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:OUT_];
    }
    if (hoverLayer_ != lastHoverLayer && hoverLayer_ != nil) {
      [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:hoverLayer_ withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:OVER_];
    }
  }
  return [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_MotionEvent_Impl *) NIL_CHK(event)) flags]) getPreventDefault];
}

- (BOOL)onMouseUpWithPlaynCoreMouse_ButtonEvent_Impl:(PlaynCoreMouse_ButtonEvent_Impl *)event {
  if (!enabled_) return NO;
  [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_ButtonEvent_Impl *) NIL_CHK(event)) flags]) setPreventDefaultWithBOOL:NO];
  if (listener__ != nil) {
    [listener__ onMouseUpWithPlaynCoreMouse_ButtonEvent:event];
  }
  if (activeLayer_ != nil) {
    [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:activeLayer_ withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:UP_];
    JreOperatorRetainedAssign(&activeLayer_, self, nil);
  }
  return [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_ButtonEvent_Impl *) NIL_CHK(event)) flags]) getPreventDefault];
}

- (BOOL)onMouseWheelScrollWithPlaynCoreMouse_WheelEvent_Impl:(PlaynCoreMouse_WheelEvent_Impl *)event {
  if (!enabled_) return NO;
  if (listener__ != nil) [listener__ onMouseWheelScrollWithPlaynCoreMouse_WheelEvent:event];
  PlaynCoreAbstractLayer *target = (activeLayer_ != nil) ? activeLayer_ : hoverLayer_;
  if (target != nil) [((id<PlaynCoreDispatcher>) NIL_CHK(dispatcher_)) dispatchWithPlaynCoreAbstractLayer:target withIOSClass:[IOSClass classWithProtocol:@protocol(PlaynCoreMouse_LayerListener)] withId:event withPlaynCoreAbstractLayer_Interaction:WHEEL_SCROLL_];
  return [((id<PlaynCoreEvents_Flags>) [((PlaynCoreMouse_WheelEvent_Impl *) NIL_CHK(event)) flags]) getPreventDefault];
}

- (id)init {
  if ((self = [super init])) {
    enabled_ = YES;
    JreOperatorRetainedAssign(&dispatcher_, self, [PlaynCoreDispatcher SINGLE]);
    JreOperatorRetainedAssign(&DOWN_, self, [[[PlaynCoreMouseImpl_$1 alloc] init] autorelease]);
    JreOperatorRetainedAssign(&UP_, self, [[[PlaynCoreMouseImpl_$2 alloc] init] autorelease]);
    JreOperatorRetainedAssign(&DRAG_, self, [[[PlaynCoreMouseImpl_$3 alloc] init] autorelease]);
    JreOperatorRetainedAssign(&MOVE_, self, [[[PlaynCoreMouseImpl_$4 alloc] init] autorelease]);
    JreOperatorRetainedAssign(&OVER_, self, [[[PlaynCoreMouseImpl_$5 alloc] init] autorelease]);
    JreOperatorRetainedAssign(&OUT_, self, [[[PlaynCoreMouseImpl_$6 alloc] init] autorelease]);
    JreOperatorRetainedAssign(&WHEEL_SCROLL_, self, [[[PlaynCoreMouseImpl_$7 alloc] init] autorelease]);
  }
  return self;
}

- (void)dealloc {
  JreOperatorRetainedAssign(&WHEEL_SCROLL_, self, nil);
  JreOperatorRetainedAssign(&OUT_, self, nil);
  JreOperatorRetainedAssign(&OVER_, self, nil);
  JreOperatorRetainedAssign(&MOVE_, self, nil);
  JreOperatorRetainedAssign(&DRAG_, self, nil);
  JreOperatorRetainedAssign(&UP_, self, nil);
  JreOperatorRetainedAssign(&DOWN_, self, nil);
  JreOperatorRetainedAssign(&hoverLayer_, self, nil);
  JreOperatorRetainedAssign(&activeLayer_, self, nil);
  JreOperatorRetainedAssign(&listener__, self, nil);
  JreOperatorRetainedAssign(&dispatcher_, self, nil);
  [super dealloc];
}

- (void)copyAllPropertiesTo:(id)copy {
  [super copyAllPropertiesTo:copy];
  PlaynCoreMouseImpl *typedCopy = (PlaynCoreMouseImpl *) copy;
  typedCopy.enabled = enabled_;
  typedCopy.dispatcher = dispatcher_;
  typedCopy.listener_ = listener__;
  typedCopy.activeLayer = activeLayer_;
  typedCopy.hoverLayer = hoverLayer_;
  typedCopy.DOWN = DOWN_;
  typedCopy.UP = UP_;
  typedCopy.DRAG = DRAG_;
  typedCopy.MOVE = MOVE_;
  typedCopy.OVER = OVER_;
  typedCopy.OUT = OUT_;
  typedCopy.WHEEL_SCROLL = WHEEL_SCROLL_;
}

@end
@implementation PlaynCoreMouseImpl_$1

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_ButtonEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseDownWithPlaynCoreMouse_ButtonEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end
@implementation PlaynCoreMouseImpl_$2

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_ButtonEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseUpWithPlaynCoreMouse_ButtonEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end
@implementation PlaynCoreMouseImpl_$3

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_MotionEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseDragWithPlaynCoreMouse_MotionEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end
@implementation PlaynCoreMouseImpl_$4

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_MotionEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseMoveWithPlaynCoreMouse_MotionEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end
@implementation PlaynCoreMouseImpl_$5

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_MotionEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseOverWithPlaynCoreMouse_MotionEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end
@implementation PlaynCoreMouseImpl_$6

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_MotionEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseOutWithPlaynCoreMouse_MotionEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end
@implementation PlaynCoreMouseImpl_$7

- (void)interactWithId:(id<PlaynCoreMouse_LayerListener>)l
                withId:(PlaynCoreMouse_WheelEvent_Impl *)ev {
  [((id<PlaynCoreMouse_LayerListener>) NIL_CHK(l)) onMouseWheelScrollWithPlaynCoreMouse_WheelEvent:ev];
}

- (id)init {
  return [super init];
}

- (void)dealloc {
  [super dealloc];
}

@end