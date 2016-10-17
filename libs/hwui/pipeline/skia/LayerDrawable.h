/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#pragma once

#include "Layer.h"

#include <SkCanvas.h>
#include <SkDrawable.h>

namespace android {
namespace uirenderer {
namespace skiapipeline {

/*
 * Draws a layer backed by an OpenGL texture into a SkCanvas.
 */
class LayerDrawable : public SkDrawable {
 public:
    explicit LayerDrawable(Layer* layer)
            : mLayer(layer) {}

 protected:
     virtual SkRect onGetBounds() override {
         return SkRect::MakeWH(mLayer->getWidth(), mLayer->getHeight());
     }
     virtual void onDraw(SkCanvas* canvas) override;

private:
    sp<Layer> mLayer;
};

}; // namespace skiapipeline
}; // namespace uirenderer
}; // namespace android
