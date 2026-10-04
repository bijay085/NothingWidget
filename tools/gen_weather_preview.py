"""Crisp Weather picker preview — 2048px vector/font draw (never screenshot upscale)."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(r"D:\NothingWidget")
SIZE = 2048
OUT = ROOT / "app" / "src" / "widgets" / "weather" / "res" / "drawable-nodpi" / "weather_preview.png"
for old in [
    ROOT / "app" / "src" / "widgets" / "weather" / "res" / "drawable" / "weather_preview.png",
    ROOT / "app" / "src" / "main" / "res" / "drawable" / "weather_preview.png",
]:
    if old.exists():
        old.unlink()

img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
d = ImageDraw.Draw(img)
card = (72, 72, SIZE - 72, SIZE - 72)
r = 280

sh = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
ImageDraw.Draw(sh).rounded_rectangle((78, 96, SIZE - 70, SIZE - 60), radius=r, fill=(0, 0, 0, 70))
sh = sh.filter(ImageFilter.GaussianBlur(28))
img = Image.alpha_composite(img, sh)
d = ImageDraw.Draw(img)
d.rounded_rectangle(card, radius=r, fill=(36, 59, 92, 255))

x, y = SIZE // 2, 420
cloud, rain = (245, 248, 255, 255), (111, 168, 255, 255)
d.ellipse((x - 190, y - 20, x + 20, y + 190), fill=cloud)
d.ellipse((x - 80, y - 110, x + 140, y + 110), fill=cloud)
d.ellipse((x + 40, y - 30, x + 220, y + 180), fill=cloud)
d.rounded_rectangle((x - 200, y + 60, x + 210, y + 200), radius=84, fill=cloud)
for i, ox in enumerate((-70, 10, 90)):
    d.line((x + ox, y + 230 + i * 4, x + ox + 56, y + 314 + i * 4), fill=rain, width=18)

ft = ImageFont.truetype(r"C:\Windows\Fonts\segoeuib.ttf", 300)
fu = ImageFont.truetype(r"C:\Windows\Fonts\seguisb.ttf", 100)
fc = ImageFont.truetype(r"C:\Windows\Fonts\segoeui.ttf", 108)
fl = ImageFont.truetype(r"C:\Windows\Fonts\segoeui.ttf", 88)
W = (255, 255, 255, 255)
left, yt = 216, 940
d.text((left, yt), "21°", font=ft, fill=W)
tb = d.textbbox((left, yt), "21°", font=ft)
ub = d.textbbox((0, 0), "C", font=fu)
d.text((tb[2] + 16, tb[3] - (ub[3] - ub[1]) - 24), "C", font=fu, fill=(208, 214, 229, 255))
d.text((left, 1310), "Rain", font=fc, fill=(208, 214, 229, 255))
d.text((left, 1470), "New Baneshwor", font=fl, fill=(165, 176, 197, 255))

OUT.parent.mkdir(parents=True, exist_ok=True)
img.save(OUT, "PNG")
print("wrote", OUT, img.size)
