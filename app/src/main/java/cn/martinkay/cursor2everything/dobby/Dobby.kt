package cn.martinkay.cursor2everything.dobby

object Dobby {
    external fun setStatus(status: Boolean)

    external fun hook(): Int
    external fun unHook(): Int
}