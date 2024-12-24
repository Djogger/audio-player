from pathlib import Path
import torch
import sys
import os
import torchaudio
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from model.inference import infer, process_stereo
from model.model import DynamicNsNet2

ROOT_DIR = Path(__file__).resolve().parents[1]
FILES_DIR = ROOT_DIR / 'files'


def process_audio():
    model = DynamicNsNet2(num_features=257)
    model.load_state_dict(torch.load(f"{FILES_DIR}/nsnet2_2.pth", map_location=torch.device('cpu')))

    file_path = f"{FILES_DIR}/received_audio.mp3"
    with torch.no_grad():
        waveform, sr = torchaudio.load(file_path)
        shape = waveform.shape[0]
        if shape == 1:
            denoised_audio = infer(model, file_path, device="cuda" if torch.cuda.is_available() else "cpu")
        else:
            denoised_audio = process_stereo(model, file_path, device="cuda" if torch.cuda.is_available() else "cpu")

    output_path = f"{FILES_DIR}/denoised_audio.mp3"
    if shape == 1:
        torchaudio.save(output_path, denoised_audio.unsqueeze(0).cpu(), sample_rate=16000)
    else:
        torchaudio.save(output_path, denoised_audio.cpu(), sample_rate=16000)
    print(f"Denoised audio saved as {output_path}")
