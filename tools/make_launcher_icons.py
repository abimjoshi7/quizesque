#!/usr/bin/env python3
"""
Renders the QUIZesque legacy launcher icons (API 24-25, pre-adaptive) from the
same geometry as drawable/ic_launcher_foreground.xml, so the square and
adaptive icons stay the same mark.

Usage: python3 tools/make_launcher_icons.py [--preview out.png]
"""
import math
import os
import sys

from PIL import Image, ImageDraw

RES = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res")

DENSITIES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

SS = 4  # supersample factor

GOLD_DEEP = (126, 90, 27)
GOLD = (232, 180, 81)
GOLD_PALE = (255, 246, 220)
BINDU = (245, 220, 154)

TAIL_DEG = 45.0
GAP_DEG = 46.0

# legacy icons are shown edge to edge, so the mark runs larger than in the
# adaptive foreground (which loses 18dp per side to the mask)
R = 34.0
W = 0.27 * R
CX = CY = 54.0


def _lerp(a, b, t):
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))


def _background(size, rounded=True):
    """Dark studio bed with a warm pool of light behind the mark."""
    s = size * SS
    k = s / 108.0
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))

    # lit from the centre, falling off to near-black
    grad = Image.new("RGBA", (s, s))
    gd = ImageDraw.Draw(grad)
    for y in range(s):
        t = y / max(1, s - 1)
        gd.line([(0, y), (s, y)], fill=_lerp((36, 45, 52), (4, 6, 10), t) + (255,))
    img.alpha_composite(grad)

    # warm glow
    glow = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    gdr = ImageDraw.Draw(glow)
    steps = 52
    for i in range(steps, 0, -1):
        t = i / steps
        rad = 54.0 * t * k
        alpha = int(104 * (1 - t) ** 1.5)
        gdr.ellipse(
            [CX * k - rad, CY * k - rad, CX * k + rad, CY * k + rad],
            fill=(138, 98, 32, alpha),
        )
    img.alpha_composite(glow)

    # clip to shape
    mask = Image.new("L", (s, s), 0)
    md = ImageDraw.Draw(mask)
    if rounded:
        md.rounded_rectangle([0, 0, s - 1, s - 1], radius=24 * k, fill=255)
    else:
        md.ellipse([0, 0, s - 1, s - 1], fill=255)
    out = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    out.paste(img, (0, 0), mask)
    return out


GOLD_RAMP = ((248, 214, 140), (238, 186, 88), (212, 150, 48), (170, 112, 24))
KEY_LIGHT_DEG = 225.0  # upper left


def _gold_at_angle(deg):
    """Gold lit from KEY_LIGHT_DEG: pale where it faces the light, deep opposite."""
    d = abs((deg - KEY_LIGHT_DEG + 180.0) % 360.0 - 180.0)  # 0..180
    lit = 0.5 + 0.5 * math.cos(math.radians(d))               # 1 facing light, 0 opposite
    lit = 0.12 + 0.88 * lit ** 1.7                             # keep it gold, never white
    pos = (1.0 - lit) * (len(GOLD_RAMP) - 1)
    i = min(len(GOLD_RAMP) - 2, int(pos))
    return _lerp(GOLD_RAMP[i], GOLD_RAMP[i + 1], pos - i)


def _cap(d, pt, width, colour, alpha=255):
    """Round line cap — PIL has no cap style, so stamp a disc."""
    r = width / 2.0
    x, y = pt
    d.ellipse([x - r, y - r, x + r, y + r], fill=colour + (alpha,))


def _mark(img):
    k = img.width / 108.0
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)

    def pol(deg, rad):
        a = math.radians(deg)
        return (CX * k + rad * k * math.cos(a), CY * k + rad * k * math.sin(a))

    start = TAIL_DEG + GAP_DEG / 2.0
    end = start + 360.0 - GAP_DEG

    def ring_box(width_px):
        # PIL grows an arc's stroke inwards from the bounding box, so grow the
        # box by half the width to land the stroke centreline on radius R.
        grow = width_px / 2.0
        return [
            (CX - R) * k - grow,
            (CY - R) * k - grow,
            (CX + R) * k + grow,
            (CY + R) * k + grow,
        ]

    # drop shadow, so the mark reads as raised off the bed
    sw = W * 1.44 * k
    shadow = Image.new("RGBA", img.size, (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.arc(ring_box(sw), start=start, end=end, fill=(0, 0, 0, 78), width=int(round(sw)))
    _cap(sd, pol(start, R), sw, (0, 0, 0), 78)
    _cap(sd, pol(end % 360, R), sw, (0, 0, 0), 78)
    shadow = shadow.transform(
        img.size, Image.AFFINE, (1, 0, 0, 0, 1, -1.5 * k), resample=Image.BILINEAR
    )
    img.alpha_composite(shadow)

    # deep base, only just proud of the gold
    dw = W * 1.44 * k
    deep = (96, 66, 14, 190)
    d.arc(ring_box(dw), start=start, end=end, fill=deep, width=int(round(dw)))
    _cap(d, pol(start, R), dw, deep[:3], deep[3])
    _cap(d, pol(end % 360, R), dw, deep[:3], deep[3])

    # metallic gold, lit along the sweep
    gw = W * k
    steps = 180
    for i in range(steps):
        a0 = start + (end - start) * (i / steps)
        a1 = start + (end - start) * ((i + 1.8) / steps)
        d.arc(ring_box(gw), start=a0, end=a1,
              fill=_gold_at_angle(a0) + (255,), width=int(round(gw)))
    _cap(d, pol(start, R), gw, _gold_at_angle(start), 255)
    _cap(d, pol(end % 360, R), gw, _gold_at_angle(end), 255)

    # pale specular core — a highlight, not a line.
    # Must be opaque: ImageDraw overwrites alpha rather than blending, so a
    # translucent pass would punch a hole through the gold and read grey.
    pw = W * 0.065 * k
    spec = (255, 239, 202, 235)
    d.arc(ring_box(pw), start=start, end=end, fill=spec, width=int(round(pw)))
    _cap(d, pol(start, R), pw, spec[:3], spec[3])
    _cap(d, pol(end % 360, R), pw, spec[:3], spec[3])

    # tail, starting inside the ring so it reads as one continuous Q
    a = pol(TAIL_DEG, R * 0.58)
    b = pol(TAIL_DEG, R * 1.16)
    for mul, colour, alpha in (
        (1.44, (96, 66, 14), 190),
        (1.02, _gold_at_angle(TAIL_DEG + 20), 255),
        (0.08, (255, 239, 202), 235),
    ):
        width = W * mul * k
        d.line([a, b], fill=colour + (alpha,), width=int(round(width)))
        _cap(d, a, width, colour, alpha)
        _cap(d, b, width, colour, alpha)

    # bindu: a small gold cabochon with a dark bezel and warm inner light
    br = R * 0.185 * k
    d.ellipse([CX * k - br * 1.16, CY * k - br * 1.16,
               CX * k + br * 1.16, CY * k + br * 1.16], fill=(89, 58, 17, 255))
    d.ellipse([CX * k - br, CY * k - br, CX * k + br, CY * k + br], fill=(205, 144, 45, 255))
    d.ellipse([CX * k - br * 0.78, CY * k - br * 0.78,
               CX * k + br * 0.78, CY * k + br * 0.78], fill=BINDU + (255,))
    hr = br * 0.18
    hx = CX * k - br * 0.28
    hy = CY * k - br * 0.34
    d.ellipse([hx - hr, hy - hr, hx + hr, hy + hr], fill=(255, 255, 255, 220))

    img.alpha_composite(layer)
    return img


def render(size, rounded=True):
    img = _background(size, rounded)
    return _mark(img).resize((size, size), Image.LANCZOS)


def main():
    if "--preview" in sys.argv:
        out = sys.argv[sys.argv.index("--preview") + 1]
        sheet = Image.new("RGBA", (512 * 2 + 60, 512 + 40), (18, 18, 18, 255))
        sheet.alpha_composite(render(512, True), (20, 20))
        sheet.alpha_composite(render(512, False), (512 + 40, 20))
        sheet.save(out)
        print("wrote", out)
        return

    for density, size in DENSITIES.items():
        folder = os.path.join(RES, f"mipmap-{density}")
        os.makedirs(folder, exist_ok=True)
        render(size, True).save(os.path.join(folder, "ic_launcher.png"))
        render(size, False).save(os.path.join(folder, "ic_launcher_round.png"))
        print(f"mipmap-{density}: {size}x{size}")


if __name__ == "__main__":
    main()
