from pathlib import Path
import torch
import sys
import os
import torchaudio
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from model.inference import infer
from model.model import DynamicNsNet2

ROOT_DIR = Path(__file__).resolve().parents[1]
FILES_DIR = ROOT_DIR / 'files'


def process_audio():
    model = DynamicNsNet2(num_features=257)
    model.load_state_dict(torch.load(f"{FILES_DIR}/nsnet2_2.pth", map_location=torch.device('cpu')))

    file_path = f"{FILES_DIR}/received_audio.wav"
    with torch.no_grad():
        denoised_audio = infer(model, file_path, device="cuda" if torch.cuda.is_available() else "cpu")

    output_path = f"{FILES_DIR}/denoised_audio.wav"
    torchaudio.save(output_path, denoised_audio.unsqueeze(0).cpu(), sample_rate=16000)
    print(f"Denoised audio saved as {output_path}")