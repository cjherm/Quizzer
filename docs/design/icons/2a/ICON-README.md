# Quizzer app icon 2a

Slanted Q over a pink-orange striped sun.

Android adaptive icon (API 26+) as vector drawables, 108 × 108 dp, artwork inside the central 72 dp.

Unzip in the repository root. It writes into `app/src/main/res/`:

| File | Content |
|---|---|
| `drawable/ic_launcher_background.xml` | Background layer |
| `drawable/ic_launcher_foreground.xml` | Foreground layer (replaces the existing one) |
| `drawable/ic_launcher_monochrome.xml` | One-color layer for Android 13+ themed icons |
| `mipmap-anydpi-v26/ic_launcher.xml` | Adaptive icon (replaces the existing one) |
| `mipmap-anydpi-v26/ic_launcher_round.xml` | Same, for `android:roundIcon` |

In `AndroidManifest.xml` keep `android:icon="@mipmap/ic_launcher"` and optionally add `android:roundIcon="@mipmap/ic_launcher_round"`. The old `ic_launcher_background` color in `values/colors.xml` is no longer used and can be removed.

The Q is Anton (SIL OFL 1.1) converted to paths, so no font is needed. Vector drawables can't blur, so glows are radial gradients and semi-transparent strokes.

Not included: the 512 × 512 PNG for Google Play, and legacy PNGs for Android 7 and older (only needed if minSdk < 26).
