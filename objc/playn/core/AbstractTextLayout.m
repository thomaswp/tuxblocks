//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: playn/core/AbstractTextLayout.java
//
//  Created by Thomas on 7/1/13.
//

#import "Graphics.h"
#import "Rectangle.h"
#import "TextFormat.h"
#import "AbstractTextLayout.h"

@implementation PlaynCoreAbstractTextLayout

- (PlaynCoreTextFormat *)format_ {
  return format__;
}
- (void)setFormat_:(PlaynCoreTextFormat *)format_ {
  JreOperatorRetainedAssign(&format__, self, format_);
}
@synthesize format_ = format__;
@synthesize pad = pad_;
@synthesize width_ = width__;
@synthesize height_ = height__;

- (float)width {
  return width__ + 2 * pad_;
}

- (float)height {
  return height__ + 2 * pad_;
}

- (PlaynCoreTextFormat *)format {
  return format__;
}

- (id)initWithPlaynCoreGraphics:(id<PlaynCoreGraphics>)gfx
                   withNSString:(NSString *)text
        withPlaynCoreTextFormat:(PlaynCoreTextFormat *)format {
  if ((self = [super init])) {
    self.format_ = format;
    self.pad = 1 / [((id<PlaynCoreGraphics>) NIL_CHK(gfx)) scaleFactor];
  }
  return self;
}

- (float)ascent {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (float)descent {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (float)leading {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (PythagorasFRectangle *)lineBoundsWithInt:(int)param0 {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (int)lineCount {
  // can't call an abstract method
  [self doesNotRecognizeSelector:_cmd];
  return 0;
}

- (void)dealloc {
  JreOperatorRetainedAssign(&format__, self, nil);
  [super dealloc];
}

- (void)copyAllPropertiesTo:(id)copy {
  [super copyAllPropertiesTo:copy];
  PlaynCoreAbstractTextLayout *typedCopy = (PlaynCoreAbstractTextLayout *) copy;
  typedCopy.format_ = format__;
  typedCopy.pad = pad_;
  typedCopy.width_ = width__;
  typedCopy.height_ = height__;
}

@end