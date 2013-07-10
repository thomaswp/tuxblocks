//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: tripleplay/util/TextConfig.java
//
//  Created by Thomas on 7/1/13.
//

#import "Asserts.h"
#import "Canvas.h"
#import "CanvasImage.h"
#import "EffectRenderer.h"
#import "Font.h"
#import "Graphics.h"
#import "ImageLayer.h"
#import "PlayN.h"
#import "TextFormat.h"
#import "TextLayout.h"

@implementation TripleplayUtilTextConfig

- (PlaynCoreTextFormat *)format {
  return format_;
}
- (void)setFormat:(PlaynCoreTextFormat *)format {
  JreOperatorRetainedAssign(&format_, self, format);
}
@synthesize format = format_;
@synthesize textColor = textColor_;
- (TripleplayUtilEffectRenderer *)effect {
  return effect_;
}
- (void)setEffect:(TripleplayUtilEffectRenderer *)effect {
  JreOperatorRetainedAssign(&effect_, self, effect);
}
@synthesize effect = effect_;
@synthesize underlined = underlined_;

- (id)initWithInt:(int)textColor {
  return [self initTripleplayUtilTextConfigWithPlaynCoreTextFormat:[[[PlaynCoreTextFormat alloc] init] autorelease] withInt:textColor withTripleplayUtilEffectRenderer:[TripleplayUtilEffectRenderer NONE]];
}

- (id)initWithPlaynCoreTextFormat:(PlaynCoreTextFormat *)format
                          withInt:(int)textColor {
  return [self initTripleplayUtilTextConfigWithPlaynCoreTextFormat:format withInt:textColor withTripleplayUtilEffectRenderer:[TripleplayUtilEffectRenderer NONE]];
}

- (id)initTripleplayUtilTextConfigWithPlaynCoreTextFormat:(PlaynCoreTextFormat *)format
                                                  withInt:(int)textColor
                         withTripleplayUtilEffectRenderer:(TripleplayUtilEffectRenderer *)effect {
  return [self initTripleplayUtilTextConfigWithPlaynCoreTextFormat:format withInt:textColor withTripleplayUtilEffectRenderer:effect withBOOL:NO];
}

- (id)initWithPlaynCoreTextFormat:(PlaynCoreTextFormat *)format
                          withInt:(int)textColor
 withTripleplayUtilEffectRenderer:(TripleplayUtilEffectRenderer *)effect {
  return [self initTripleplayUtilTextConfigWithPlaynCoreTextFormat:format withInt:textColor withTripleplayUtilEffectRenderer:effect];
}

- (id)initTripleplayUtilTextConfigWithPlaynCoreTextFormat:(PlaynCoreTextFormat *)format
                                                  withInt:(int)textColor
                         withTripleplayUtilEffectRenderer:(TripleplayUtilEffectRenderer *)effect
                                                 withBOOL:(BOOL)underlined {
  if ((self = [super init])) {
    self.format = ((PlaynCoreTextFormat *) [PlaynCoreAsserts checkNotNullWithId:format]);
    self.textColor = textColor;
    self.effect = ((TripleplayUtilEffectRenderer *) [PlaynCoreAsserts checkNotNullWithId:effect]);
    self.underlined = underlined;
  }
  return self;
}

- (id)initWithPlaynCoreTextFormat:(PlaynCoreTextFormat *)format
                          withInt:(int)textColor
 withTripleplayUtilEffectRenderer:(TripleplayUtilEffectRenderer *)effect
                         withBOOL:(BOOL)underlined {
  return [self initTripleplayUtilTextConfigWithPlaynCoreTextFormat:format withInt:textColor withTripleplayUtilEffectRenderer:effect withBOOL:underlined];
}

- (BOOL)isEqual:(id)other {
  if (!([other isKindOfClass:[TripleplayUtilTextConfig class]])) return NO;
  TripleplayUtilTextConfig *that = (TripleplayUtilTextConfig *) other;
  return [((PlaynCoreTextFormat *) NIL_CHK(format_)) isEqual:((TripleplayUtilTextConfig *) NIL_CHK(that)).format] && effect_ == ((TripleplayUtilTextConfig *) NIL_CHK(that)).effect && underlined_ == ((TripleplayUtilTextConfig *) NIL_CHK(that)).underlined && textColor_ == ((TripleplayUtilTextConfig *) NIL_CHK(that)).textColor;
}

- (NSUInteger)hash {
  return [((PlaynCoreTextFormat *) NIL_CHK(format_)) hash] ^ [((TripleplayUtilEffectRenderer *) NIL_CHK(effect_)) hash] ^ (underlined_ ? 1 : 0) ^ textColor_;
}

- (TripleplayUtilTextConfig *)withFormatWithPlaynCoreTextFormat:(PlaynCoreTextFormat *)format {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:format withInt:textColor_ withTripleplayUtilEffectRenderer:effect_ withBOOL:underlined_] autorelease];
}

- (TripleplayUtilTextConfig *)withFontWithPlaynCoreFont:(id<PlaynCoreFont>)font {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:[((PlaynCoreTextFormat *) NIL_CHK(format_)) withFontWithPlaynCoreFont:font] withInt:textColor_ withTripleplayUtilEffectRenderer:effect_ withBOOL:underlined_] autorelease];
}

- (TripleplayUtilTextConfig *)withColorWithInt:(int)textColor {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:format_ withInt:textColor withTripleplayUtilEffectRenderer:effect_ withBOOL:underlined_] autorelease];
}

- (TripleplayUtilTextConfig *)withShadowWithInt:(int)shadowColor
                                      withFloat:(float)shadowX
                                      withFloat:(float)shadowY {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:format_ withInt:textColor_ withTripleplayUtilEffectRenderer:[[[TripleplayUtilEffectRenderer_Shadow alloc] initWithInt:shadowColor withFloat:shadowX withFloat:shadowY] autorelease] withBOOL:underlined_] autorelease];
}

- (TripleplayUtilTextConfig *)withOutlineWithInt:(int)outlineColor {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:format_ withInt:textColor_ withTripleplayUtilEffectRenderer:[[[TripleplayUtilEffectRenderer_PixelOutline alloc] initWithInt:outlineColor] autorelease] withBOOL:underlined_] autorelease];
}

- (TripleplayUtilTextConfig *)withOutlineWithInt:(int)outlineColor
                                       withFloat:(float)outlineWidth {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:format_ withInt:textColor_ withTripleplayUtilEffectRenderer:[[[TripleplayUtilEffectRenderer_VectorOutline alloc] initWithInt:outlineColor withFloat:outlineWidth] autorelease] withBOOL:underlined_] autorelease];
}

- (TripleplayUtilTextConfig *)withUnderlineWithBOOL:(BOOL)underlined {
  return [[[TripleplayUtilTextConfig alloc] initWithPlaynCoreTextFormat:format_ withInt:textColor_ withTripleplayUtilEffectRenderer:effect_ withBOOL:underlined] autorelease];
}

- (id<PlaynCoreTextLayout>)layoutWithNSString:(NSString *)text {
  return [((id<PlaynCoreGraphics>) [PlaynCorePlayN graphics]) layoutTextWithNSString:text withPlaynCoreTextFormat:format_];
}

- (id<PlaynCoreCanvasImage>)createImageWithPlaynCoreTextLayout:(id<PlaynCoreTextLayout>)layout {
  return [((id<PlaynCoreGraphics>) [PlaynCorePlayN graphics]) createImageWithFloat:[((TripleplayUtilEffectRenderer *) NIL_CHK(effect_)) adjustWidthWithFloat:[((id<PlaynCoreTextLayout>) NIL_CHK(layout)) width]] withFloat:[((TripleplayUtilEffectRenderer *) NIL_CHK(effect_)) adjustHeightWithFloat:[((id<PlaynCoreTextLayout>) NIL_CHK(layout)) height]]];
}

- (void)renderWithPlaynCoreCanvas:(id<PlaynCoreCanvas>)canvas
          withPlaynCoreTextLayout:(id<PlaynCoreTextLayout>)layout
                        withFloat:(float)x
                        withFloat:(float)y {
  [((TripleplayUtilEffectRenderer *) NIL_CHK(effect_)) renderWithPlaynCoreCanvas:canvas withPlaynCoreTextLayout:layout withInt:textColor_ withBOOL:underlined_ withFloat:x withFloat:y];
}

- (id<PlaynCoreCanvasImage>)toImageWithNSString:(NSString *)text {
  return [self toImageWithPlaynCoreTextLayout:[self layoutWithNSString:text]];
}

- (id<PlaynCoreCanvasImage>)toImageWithPlaynCoreTextLayout:(id<PlaynCoreTextLayout>)layout {
  id<PlaynCoreCanvasImage> image = [self createImageWithPlaynCoreTextLayout:layout];
  [self renderWithPlaynCoreCanvas:[((id<PlaynCoreCanvasImage>) NIL_CHK(image)) canvas] withPlaynCoreTextLayout:layout withFloat:0 withFloat:0];
  return image;
}

- (id<PlaynCoreImageLayer>)toLayerWithNSString:(NSString *)text {
  return [self toLayerWithPlaynCoreTextLayout:[self layoutWithNSString:text]];
}

- (id<PlaynCoreImageLayer>)toLayerWithPlaynCoreTextLayout:(id<PlaynCoreTextLayout>)layout {
  return [((id<PlaynCoreGraphics>) [PlaynCorePlayN graphics]) createImageLayerWithPlaynCoreImage:[self toImageWithPlaynCoreTextLayout:layout]];
}

- (void)dealloc {
  JreOperatorRetainedAssign(&effect_, self, nil);
  JreOperatorRetainedAssign(&format_, self, nil);
  [super dealloc];
}

- (void)copyAllPropertiesTo:(id)copy {
  [super copyAllPropertiesTo:copy];
  TripleplayUtilTextConfig *typedCopy = (TripleplayUtilTextConfig *) copy;
  typedCopy.format = format_;
  typedCopy.textColor = textColor_;
  typedCopy.effect = effect_;
  typedCopy.underlined = underlined_;
}

@end