#!/usr/bin/env python3
"""Create thesis experiment assets from a PlantVillage-style dataset tree.

This script generates:

- fixed `train/val/test/demo` CSV split files for the 38 labels used by the app;
- local CSV templates for inference, real-world and out-of-domain evaluation;
- a machine-readable summary of any classes that do not meet the requested quotas.
"""

from __future__ import annotations

import argparse
import csv
import json
import random
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


LABELS = [
    "Apple___Apple_scab",
    "Apple___Black_rot",
    "Apple___Cedar_apple_rust",
    "Apple___healthy",
    "Blueberry___healthy",
    "Cherry___healthy",
    "Cherry___Powdery_mildew",
    "Corn___Cercospora_leaf_spot Gray_leaf_spot",
    "Corn___Common_rust",
    "Corn___healthy",
    "Corn___Northern_Leaf_Blight",
    "Grape___Black_rot",
    "Grape___Esca_(Black_Measles)",
    "Grape___healthy",
    "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
    "Orange___Haunglongbing_(Citrus_greening)",
    "Peach___Bacterial_spot",
    "Peach___healthy",
    "Pepper,_bell___Bacterial_spot",
    "Pepper,_bell___healthy",
    "Potato___Early_blight",
    "Potato___healthy",
    "Potato___Late_blight",
    "Raspberry___healthy",
    "Soybean___healthy",
    "Squash___Powdery_mildew",
    "Strawberry___healthy",
    "Strawberry___Leaf_scorch",
    "Tomato___Bacterial_spot",
    "Tomato___Early_blight",
    "Tomato___healthy",
    "Tomato___Late_blight",
    "Tomato___Leaf_Mold",
    "Tomato___Septoria_leaf_spot",
    "Tomato___Spider_mites Two-spotted_spider_mite",
    "Tomato___Target_Spot",
    "Tomato___Tomato_mosaic_virus",
    "Tomato___Tomato_Yellow_Leaf_Curl_Virus",
]

DEMO_CLASSES = [
    "Apple___Apple_scab",
    "Corn___Common_rust",
    "Potato___Late_blight",
    "Grape___Black_rot",
    "Tomato___Bacterial_spot",
    "Tomato___healthy",
]

IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp"}
VALIDATION_PER_CLASS = 10
TEST_PER_CLASS = 30
DEMO_PER_CLASS = 2


@dataclass(frozen=True)
class SplitRow:
    image_id: str
    ruta_origen: str
    clase: str
    uso: str
    modelo_aplicable: str
    observacion: str


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--dataset-root",
        type=Path,
        help="Path to the PlantVillage dataset root where each class is stored in its own folder.",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=Path("experiments/generated"),
        help="Directory where the generated CSV files and JSON summary will be written.",
    )
    parser.add_argument(
        "--seed",
        type=int,
        default=42,
        help="Deterministic random seed used to create fixed splits.",
    )
    return parser.parse_args()


def collect_images(class_dir: Path) -> list[Path]:
    if not class_dir.exists():
        return []
    images = [
        path for path in sorted(class_dir.iterdir())
        if path.is_file() and path.suffix.lower() in IMAGE_EXTENSIONS
    ]
    return images


def make_image_id(label: str, index: int) -> str:
    safe_label = (
        label.replace("___", "__")
        .replace(" ", "_")
        .replace(",", "")
        .replace("(", "")
        .replace(")", "")
    )
    return f"{safe_label}_{index:04d}"


def select_demo_rows(test_rows: Iterable[SplitRow]) -> list[SplitRow]:
    grouped: dict[str, list[SplitRow]] = {}
    for row in test_rows:
        grouped.setdefault(row.clase, []).append(row)

    demo_rows: list[SplitRow] = []
    for class_name in DEMO_CLASSES:
        candidates = grouped.get(class_name, [])
        for idx, row in enumerate(candidates[:DEMO_PER_CLASS]):
            difficulty = "easy" if idx == 0 else "moderate"
            demo_rows.append(
                SplitRow(
                    image_id=row.image_id,
                    ruta_origen=row.ruta_origen,
                    clase=row.clase,
                    uso="demo",
                    modelo_aplicable=row.modelo_aplicable,
                    observacion=f"demo_{difficulty}",
                )
            )
    return demo_rows


def write_csv(path: Path, header: list[str], rows: Iterable[Iterable[str]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(header)
        writer.writerows(rows)


def generate_templates(output_dir: Path) -> None:
    templates = {
        "00_resumen.csv": [
            [
                "modelo", "version", "dataset", "accuracy", "precision_macro",
                "recall_macro", "f1_macro", "tiempo_promedio_ms", "p95_ms",
                "memoria_mb", "bateria_delta_pct", "conclusion",
            ]
        ],
        "01_splits_imagenes.csv": [
            [
                "image_id", "ruta_origen", "clase", "uso",
                "modelo_aplicable", "observacion",
            ]
        ],
        "02_resultados_modelo.csv": [
            [
                "run_id", "modelo", "num_test", "accuracy", "precision_macro",
                "recall_macro", "f1_macro", "precision_weighted",
                "recall_weighted", "f1_weighted", "threshold", "rechazadas",
                "clases_peor_f1",
            ]
        ],
        "03_resultados_inferencia.csv": [
            [
                "run_id", "modelo", "sesion", "device", "acelerador",
                "num_runs", "init_ms", "warmup_ms", "avg_ms", "median_ms",
                "p95_ms", "std_ms", "mem_init_mb", "mem_total_mb",
                "battery_before", "battery_after", "delta_battery",
            ]
        ],
        "04_real_world.csv": [
            [
                "image_id", "condicion_captura", "sintomas_visibles",
                "prediccion_top1", "confianza", "tiempo_ms", "rechazada",
                "coherencia_visual", "comentario",
            ]
        ],
        "05_out_of_domain.csv": [
            [
                "image_id", "tipo_entrada", "prediccion_top1", "confianza",
                "tiempo_ms", "rechazada", "falso_positivo_alto", "comentario",
            ]
        ],
    }

    for filename, matrix in templates.items():
        write_csv(output_dir / filename, matrix[0], [])


def build_splits(dataset_root: Path, seed: int) -> tuple[list[SplitRow], dict[str, dict[str, int]]]:
    rng = random.Random(seed)
    split_rows: list[SplitRow] = []
    class_summary: dict[str, dict[str, int]] = {}

    for label in LABELS:
        class_dir = dataset_root / label
        images = collect_images(class_dir)
        rng.shuffle(images)

        available = len(images)
        val_count = min(VALIDATION_PER_CLASS, available)
        remaining = max(available - val_count, 0)
        test_count = min(TEST_PER_CLASS, remaining)

        val_images = images[:val_count]
        test_images = images[val_count: val_count + test_count]
        train_images = images[val_count + test_count:]

        class_summary[label] = {
            "available": available,
            "validation_selected": len(val_images),
            "test_selected": len(test_images),
            "train_selected": len(train_images),
            "required_for_full_protocol": VALIDATION_PER_CLASS + TEST_PER_CLASS,
        }

        for usage, image_group in (
            ("val", val_images),
            ("test", test_images),
            ("train", train_images),
        ):
            for index, image_path in enumerate(image_group, start=1):
                split_rows.append(
                    SplitRow(
                        image_id=make_image_id(label, index),
                        ruta_origen=str(image_path.resolve()),
                        clase=label,
                        uso=usage,
                        modelo_aplicable="MobileNetV2;EfficientNetB0",
                        observacion="",
                    )
                )

    demo_rows = select_demo_rows(row for row in split_rows if row.uso == "test")
    split_rows.extend(demo_rows)
    return split_rows, class_summary


def main() -> int:
    args = parse_args()
    output_dir = args.output_dir
    output_dir.mkdir(parents=True, exist_ok=True)

    generate_templates(output_dir)

    if not args.dataset_root:
        print("Templates created. Provide --dataset-root to generate fixed PlantVillage splits.")
        return 0

    split_rows, class_summary = build_splits(args.dataset_root, args.seed)

    write_csv(
        output_dir / "01_splits_imagenes.csv",
        ["image_id", "ruta_origen", "clase", "uso", "modelo_aplicable", "observacion"],
        [
            [
                row.image_id,
                row.ruta_origen,
                row.clase,
                row.uso,
                row.modelo_aplicable,
                row.observacion,
            ]
            for row in split_rows
        ],
    )

    with (output_dir / "split_summary.json").open("w", encoding="utf-8") as handle:
        json.dump(
            {
                "seed": args.seed,
                "dataset_root": str(args.dataset_root.resolve()),
                "validation_per_class": VALIDATION_PER_CLASS,
                "test_per_class": TEST_PER_CLASS,
                "demo_per_class": DEMO_PER_CLASS,
                "classes": class_summary,
            },
            handle,
            indent=2,
            ensure_ascii=True,
        )

    print(f"Generated split files in {output_dir}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
