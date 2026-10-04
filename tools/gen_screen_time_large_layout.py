from pathlib import Path


def font_views(prefix: str, style_name: str, mock_str: str) -> str:
    variants = [
        ("", "Clean", False),
        ("_digital", "Digital", True),
        ("_futuristic", "Futuristic", True),
        ("_pixel", "PixelMono", True),
        ("_spooky", "DarkSpooky", True),
    ]
    chunks = []
    for suffix, style, gone in variants:
        vis = '\n                android:visibility="gone"' if gone else ""
        chunks.append(
            f"""            <TextView
                android:id="@+id/{prefix}{suffix}"
                style="@style/{style_name}.{style}"
                android:text="@string/{mock_str}"{vis} />"""
        )
    return "\n\n".join(chunks)


rows = []
for i in range(1, 13):
    top = "8dp" if i == 1 else "5dp"
    mock_i = ((i - 1) % 3) + 1
    name = font_views(
        f"screen_time_large_app{i}_name",
        "ScreenTimeLargeAppName",
        f"widget_screen_time_large_mock_app{mock_i}",
    )
    time = font_views(
        f"screen_time_large_app{i}_time",
        "ScreenTimeLargeAppTime",
        f"widget_screen_time_large_mock_time{mock_i}",
    )
    rows.append(
        f"""    <LinearLayout
        android:id="@+id/screen_time_large_app{i}_row"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="{top}"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <FrameLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1">

{name}
        </FrameLayout>

        <FrameLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content">

{time}
        </FrameLayout>
    </LinearLayout>"""
    )

header = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/widget_screen_time_large_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/screen_time_large_background"
    android:clickable="true"
    android:focusable="true"
    android:orientation="vertical"
    android:clipChildren="false"
    android:clipToPadding="false"
    android:paddingStart="18dp"
    android:paddingTop="12dp"
    android:paddingEnd="18dp"
    android:paddingBottom="12dp">

    <FrameLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">

        <TextView
            android:id="@+id/screen_time_large_label"
            style="@style/ScreenTimeLargeLabel.Clean" />

        <TextView
            android:id="@+id/screen_time_large_label_digital"
            style="@style/ScreenTimeLargeLabel.Digital"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_label_futuristic"
            style="@style/ScreenTimeLargeLabel.Futuristic"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_label_pixel"
            style="@style/ScreenTimeLargeLabel.PixelMono"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_label_spooky"
            style="@style/ScreenTimeLargeLabel.DarkSpooky"
            android:visibility="gone" />
    </FrameLayout>

    <FrameLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="2dp">

        <TextView
            android:id="@+id/screen_time_large_total"
            style="@style/ScreenTimeLargeTotal.Clean" />

        <TextView
            android:id="@+id/screen_time_large_total_digital"
            style="@style/ScreenTimeLargeTotal.Digital"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_total_futuristic"
            style="@style/ScreenTimeLargeTotal.Futuristic"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_total_pixel"
            style="@style/ScreenTimeLargeTotal.PixelMono"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_total_spooky"
            style="@style/ScreenTimeLargeTotal.DarkSpooky"
            android:visibility="gone" />
    </FrameLayout>

    <FrameLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">

        <TextView
            android:id="@+id/screen_time_large_section"
            style="@style/ScreenTimeLargeSection.Clean" />

        <TextView
            android:id="@+id/screen_time_large_section_digital"
            style="@style/ScreenTimeLargeSection.Digital"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_section_futuristic"
            style="@style/ScreenTimeLargeSection.Futuristic"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_section_pixel"
            style="@style/ScreenTimeLargeSection.PixelMono"
            android:visibility="gone" />

        <TextView
            android:id="@+id/screen_time_large_section_spooky"
            style="@style/ScreenTimeLargeSection.DarkSpooky"
            android:visibility="gone" />
    </FrameLayout>

"""

out = Path("app/src/widgets/screen_time_large/res/layout/widget_screen_time_large.xml")
out.write_text(header + "\n\n".join(rows) + "\n</LinearLayout>\n", encoding="utf-8")
print(f"wrote {out} with {len(rows)} app rows")
