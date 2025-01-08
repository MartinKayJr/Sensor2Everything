//
// Created by Administrator on 2025/1/8.
//
#include <dobby.h>
#include <unistd.h>
#include "mouse_hook.h"
#include "Util.h"
#include "elf_util.h"
#include "dobby_hook.h"

#define LIBSF_PATH "/system/lib64/libinputservice.so"

OriginalMouseCursorControllerMoveType OriginalMove = nullptr;

void MouseCursorControllerMove(float deltaX, float deltaY) {
    LOGE("MouseCursorControllerMove: deltaX=%f, deltaY=%f", deltaX, deltaY);
    OriginalMove(deltaX, deltaY);
}


void doMouseHook() {
    SandHook::ElfImg inputService(LIBSF_PATH);

    if (!inputService.isValid()) {
        LOGE("failed to load libsensorservice");
        return;
    }

    auto mouseMove = inputService.getSymbolAddress<void *>(
            "_ZN7android21MouseCursorController4moveEff");
    if (mouseMove == nullptr) {
        LOGE("failed to load _ZN7android21MouseCursorController4moveEff");
        return;
    }

    LOGD("Dobby mouseMove::write found at %p", mouseMove);
    OriginalMove = (OriginalMouseCursorControllerMoveType)InlineHook(mouseMove, (void *)MouseCursorControllerMove);

}