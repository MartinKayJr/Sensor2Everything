//
// Created by Administrator on 2025/1/8.
//

#ifndef CURSOR2EVERYTHING_MOUSE_HOOK_H
#define CURSOR2EVERYTHING_MOUSE_HOOK_H

typedef void (*OriginalMouseCursorControllerMoveType)(float deltaX, float deltaY);

void doMouseHook();

#endif //CURSOR2EVERYTHING_MOUSE_HOOK_H
