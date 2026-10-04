"""Crisp Round Clock picker preview — 2048px vector/font draw (never screenshot upscale)."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(r"D:\NothingWidget")
SIZE = 2048
OUT = ROOT / "app" / "src" / "widgets" / "round_clock" / "res" / "drawable-nodpi" / "round_clock_preview.png"
old = ROOT / "app" / "src" / "widgets" / "round_clock" / "res" / "drawable" / "round_clock_preview.png"
if old.exists():
    old.unlink()

img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
d = ImageDraw.Draw(img)
m = 24
circ = [m, m, SIZE - m, SIZE - m]
d.ellipse(circ, fill=(0, 0, 0, 255))

f = ImageFont.truetype(r"C:\Windows\Fonts\segoeuib.ttf", 350)
f2 = ImageFont.truetype(r"C:\Windows\Fonts\seguisb.ttf", 88)
f3 = ImageFont.truetype(r"C:\Windows\Fonts\segoeui.ttf", 110)
f4 = ImageFont.truetype(r"C:\Windows\Fonts\segoeuil.ttf", 96)
W, R, A = (255, 255, 255, 255), (255, 59, 59, 255), (242, 242, 242, 255)

left, top = 340, 400
d.text((left, top), "01", font=f, fill=W)
d.text((left, top + 360), "53", font=f, fill=W)
mb = d.textbbox((left, top + 360), "53", font=f)
ab = d.textbbox((0, 0), "PM", font=f2)
d.text((mb[2] + 28, mb[3] - (ab[3] - ab[1]) - 28), "PM", font=f2, fill=W)

right, day_y = SIZE - 370, 470
for t, y in (("Sun", day_y), ("04", day_y + 140)):
    bb = d.textbbox((0, 0), t, font=f3)
    d.text((right - (bb[2] - bb[0]), y), t, font=f3, fill=W)
d.rounded_rectangle((right - 110, day_y + 280, right, day_y + 292), radius=3, fill=R)

cx = SIZE // 2
bx, by = cx - 230, 1400
d.ellipse((bx, by + 12, bx + 68, by + 72), outline=R, width=5)
d.arc((bx + 8, by, bx + 60, by + 40), 200, 340, fill=R, width=5)
d.rectangle((bx + 28, by + 4, bx + 40, by + 16), fill=R)
d.text((bx + 90, by + 8), "07:55 AM", font=f4, fill=A)

glow = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
gd = ImageDraw.Draw(glow)
gd.arc((370, 1580, SIZE - 370, SIZE + 80), 200, 340, fill=(255, 30, 30, 120), width=60)
gd.arc((430, 1620, SIZE - 430, SIZE + 40), 205, 335, fill=(255, 70, 70, 170), width=30)
gd.arc((490, 1660, SIZE - 490, SIZE - 8), 210, 330, fill=(255, 100, 100, 230), width=14)
glow = glow.filter(ImageFilter.GaussianBlur(16))
img = Image.alpha_composite(img, glow)

mask = Image.new("L", (SIZE, SIZE), 0)
ImageDraw.Draw(mask).ellipse(circ, fill=255)
out = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
out.paste(img, mask=mask)
OUT.parent.mkdir(parents=True, exist_ok=True)
out.save(OUT, "PNG")
print("wrote", OUT, out.size)
