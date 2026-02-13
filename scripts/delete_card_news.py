#!/usr/bin/env python3
import json
import math
import sys
import urllib.error
import urllib.request
from pathlib import Path

# ===== Config =====
TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxMDM3Iiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzcwOTkzODI2LCJleHAiOjE3NzM1ODU4MjZ9.pU3je1TqgYydcLfIUQ35zMDIuO4QbCT0jslc-11fBs3kJM8NxHTKV4Hxyhc7rDqZ"
BASE_URL = "http://localhost:8080"
ENDPOINT = "/debug/news/card-news/batch-delete"
INPUT_JSON_PATH = Path(__file__).with_name("dup_card_news.json")
BATCH_SIZE = 500
TIMEOUT_SECONDS = 30


def load_ids(path: Path) -> list[int]:
    with path.open("r", encoding="utf-8") as f:
        data = json.load(f)

    # expected: {"results":[{"id":1}, ...]}
    if isinstance(data, dict) and isinstance(data.get("results"), list):
        ids = [item.get("id") for item in data["results"] if isinstance(item, dict)]
    # fallback: [1,2,3] or [{"id":1}, ...]
    elif isinstance(data, list):
        ids = []
        for item in data:
            if isinstance(item, int):
                ids.append(item)
            elif isinstance(item, dict):
                ids.append(item.get("id"))
            else:
                ids.append(None)
    else:
        raise ValueError("Unsupported JSON format for card news IDs")

    cleaned = [int(x) for x in ids if x is not None]
    # dedupe while preserving order
    unique_ids = list(dict.fromkeys(cleaned))
    return unique_ids


def post_batch_delete(ids: list[int]) -> tuple[int, str]:
    url = BASE_URL + ENDPOINT
    body = json.dumps(ids).encode("utf-8")
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {TOKEN}",
    }
    req = urllib.request.Request(url=url, data=body, headers=headers, method="POST")
    with urllib.request.urlopen(req, timeout=TIMEOUT_SECONDS) as res:
        text = res.read().decode("utf-8", errors="replace")
        return res.getcode(), text


def main() -> int:
    if TOKEN == "PASTE_TOKEN_HERE":
        print("Set TOKEN constant first.")
        return 1

    ids = load_ids(INPUT_JSON_PATH)
    if not ids:
        print("No IDs to delete.")
        return 0

    total_batches = math.ceil(len(ids) / BATCH_SIZE)
    print(f"Loaded {len(ids)} ids, sending {total_batches} batch(es).")

    for i in range(0, len(ids), BATCH_SIZE):
        batch_no = i // BATCH_SIZE + 1
        batch = ids[i : i + BATCH_SIZE]
        try:
            status, resp_text = post_batch_delete(batch)
            print(f"[{batch_no}/{total_batches}] status={status}, size={len(batch)}")
            if resp_text:
                print(resp_text)
        except urllib.error.HTTPError as e:
            err_body = e.read().decode("utf-8", errors="replace")
            print(f"[{batch_no}/{total_batches}] HTTPError {e.code}: {err_body}")
            return 1
        except Exception as e:
            print(f"[{batch_no}/{total_batches}] Error: {e}")
            return 1

    print("Done.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
