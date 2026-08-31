"""
============================================================
QR CODE GENERATOR - COMMAND LINE INTERFACE & SCRIPT
ISO/IEC 18004 Standard Compliant - 100% Scannable Everywhere
Created by Hong Quang
============================================================
"""

import sys
import os
import zlib
import struct
import argparse
from datetime import datetime

# Configure UTF-8 for Windows Terminal
if sys.platform == "win32":
    try:
        if hasattr(sys.stdout, "reconfigure"):
            sys.stdout.reconfigure(encoding="utf-8", errors="replace")
        if hasattr(sys.stderr, "reconfigure"):
            sys.stderr.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass

# Add local directory to sys.path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import qrcode

def parse_color_to_rgb(color_str: str) -> tuple:
    """Convert color name or hex string to RGB tuple (R, G, B)."""
    color_str = color_str.strip().lower()
    named_colors = {
        "black": (0, 0, 0),
        "white": (255, 255, 255),
        "red": (239, 68, 68),
        "blue": (37, 99, 235),
        "navy": (30, 58, 138),
        "green": (22, 163, 74),
        "emerald": (16, 185, 129),
        "purple": (147, 51, 234),
        "orange": (249, 115, 22),
        "yellow": (234, 179, 8),
        "gray": (107, 114, 128),
        "darkgray": (55, 65, 81),
    }
    if color_str in named_colors:
        return named_colors[color_str]
    
    hex_val = color_str.lstrip("#")
    if len(hex_val) == 3:
        hex_val = "".join(c * 2 for c in hex_val)
    if len(hex_val) == 6:
        try:
            return tuple(int(hex_val[i:i+2], 16) for i in (0, 2, 4))
        except ValueError:
            pass
            
    return (0, 0, 0)

def write_png(matrix: list, output_path: str, scale: int = 10, fg_rgb: tuple = (0, 0, 0), bg_rgb: tuple = (255, 255, 255)):
    """Save high-quality PNG image using Python standard library zlib & struct."""
    rows = len(matrix)
    cols = len(matrix[0])
    width = cols * scale
    height = rows * scale
    
    raw_data = bytearray()
    for r in range(rows):
        line = bytearray()
        for c in range(cols):
            pixel = fg_rgb if matrix[r][c] else bg_rgb
            line.extend(pixel * scale)
        for _ in range(scale):
            raw_data.append(0)  # Filter type 0 (None)
            raw_data.extend(line)
            
    def chunk(tag: bytes, data: bytes) -> bytes:
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)
        
    png_bytes = bytearray(b'\x89PNG\r\n\x1a\n')
    png_bytes.extend(chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 2, 0, 0, 0)))
    png_bytes.extend(chunk(b'IDAT', zlib.compress(bytes(raw_data), level=9)))
    png_bytes.extend(chunk(b'IEND', b''))
    
    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    with open(output_path, 'wb') as f:
        f.write(png_bytes)
        
    return os.path.abspath(output_path)

def write_svg(matrix: list, output_path: str, scale: int = 10, fg_hex: str = "#000000", bg_hex: str = "#FFFFFF"):
    """Save scalable vector SVG format."""
    rows = len(matrix)
    cols = len(matrix[0])
    width = cols * scale
    height = rows * scale
    
    rects = []
    for r in range(rows):
        for c in range(cols):
            if matrix[r][c]:
                rects.append(f'<rect x="{c * scale}" y="{r * scale}" width="{scale}" height="{scale}" fill="{fg_hex}"/>')
                
    svg_content = f'''<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">
    <rect width="100%" height="100%" fill="{bg_hex}"/>
    {''.join(rects)}
</svg>'''
    
    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(svg_content)
    return os.path.abspath(output_path)

def generate_qr(url: str, output_path: str = "qrcode.png", box_size: int = 10, border: int = 4, fg_color: str = "black", bg_color: str = "white"):
    """
    Generate QR code from URL or text with customizable options.
    """
    url_clean = url.strip()
    if not url_clean:
        raise ValueError("URL or text content cannot be empty!")

    # Standard QR Code with Level M Error Correction
    qr = qrcode.QRCode(
        version=None,
        error_correction=qrcode.constants.ERROR_CORRECT_M,
        box_size=box_size,
        border=border,
    )
    qr.add_data(url_clean)
    qr.make(fit=True)
    matrix = qr.get_matrix()

    fg_rgb = parse_color_to_rgb(fg_color)
    bg_rgb = parse_color_to_rgb(bg_color) if bg_color != "white" else (255, 255, 255)

    if output_path.lower().endswith('.svg'):
        fg_hex = f"#{fg_rgb[0]:02x}{fg_rgb[1]:02x}{fg_rgb[2]:02x}"
        bg_hex = f"#{bg_rgb[0]:02x}{bg_rgb[1]:02x}{bg_rgb[2]:02x}"
        return write_svg(matrix, output_path, scale=box_size, fg_hex=fg_hex, bg_hex=bg_hex)
    else:
        return write_png(matrix, output_path, scale=box_size, fg_rgb=fg_rgb, bg_rgb=bg_rgb)

def interactive_mode():
    """Interactive command-line mode."""
    print("=" * 60)
    print("       🚀 QR CODE GENERATOR - BY HONG QUANG       ")
    print("=" * 60)
    
    try:
        url = input("\n👉 Enter or paste website URL / text:\n> ").strip()
        if not url:
            print("❌ No input provided. Exiting.")
            return

        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        default_filename = f"qr_{timestamp}.png"
        
        file_input = input(f"\n📁 Output filename [default: {default_filename}]: ").strip()
        output_filename = file_input if file_input else default_filename
        
        if not output_filename.lower().endswith(('.png', '.svg')):
            output_filename += ".png"

        print("\n🎨 Color Options (press Enter for default Black/White, or type: blue, red, green, #2563eb, ...):")
        fg_color = input("   - Foreground color [default: black]: ").strip() or "black"
        
        print("\n⏳ Generating high-quality QR code...")
        saved_path = generate_qr(url, output_filename, fg_color=fg_color)
        
        print("=" * 60)
        print(f"🎉 QR CODE SUCCESSFULLY GENERATED!")
        print(f"📌 Saved at: {saved_path}")
        print("✨ Created by Hong Quang")
        print("=" * 60)

        if sys.platform == "win32":
            open_choice = input("\n🖼️ Do you want to open the image now? (y/n, default y): ").strip().lower()
            if open_choice in ("", "y", "yes"):
                try:
                    os.startfile(saved_path)
                except Exception as e:
                    print(f"Could not open image automatically: {e}")

    except KeyboardInterrupt:
        print("\n\nOperation cancelled.")
    except Exception as e:
        print(f"\n❌ Error: {e}")

def main():
    parser = argparse.ArgumentParser(
        description="Generate standard QR codes from URLs or text (Created by Hong Quang)."
    )
    parser.add_argument("url", nargs="?", help="Website link or content to encode into QR")
    parser.add_argument("-o", "--output", default=None, help="Output image filename (e.g. my_qr.png or my_qr.svg)")
    parser.add_argument("--size", type=int, default=10, help="Pixel size of each QR code box (default: 10)")
    parser.add_argument("--border", type=int, default=4, help="Border margin size (default: 4)")
    parser.add_argument("--fg", default="black", help="Foreground color (e.g. black, blue, red, #1e40af)")
    parser.add_argument("--bg", default="white", help="Background color (default: white)")

    args = parser.parse_args()

    if args.url:
        output_file = args.output if args.output else "qrcode.png"
        try:
            saved_path = generate_qr(args.url, output_file, args.size, args.border, args.fg, args.bg)
            print(f"✅ QR Code created successfully: {saved_path}")
        except Exception as e:
            print(f"❌ Error: {e}")
            sys.exit(1)
    else:
        interactive_mode()

if __name__ == "__main__":
    main()
