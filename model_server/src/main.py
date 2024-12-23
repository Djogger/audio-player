import asyncio
import os
from contextlib import asynccontextmanager
from pathlib import Path
from fastapi import FastAPI
from process_audio import process_audio


ROOT_DIR = Path(__file__).resolve().parents[1]
FILES_DIR = ROOT_DIR / 'files'

async def handle_client(reader: asyncio.StreamReader, writer: asyncio.StreamWriter):
    try:
        data = b""
        while True:
            try:
                chunk = await asyncio.wait_for(reader.read(1024 * 1024), timeout=2.0)
                if not chunk:
                    break
                data += chunk
            except asyncio.TimeoutError:
                print("Timeout reached while waiting for data. Ending connection.")
                break

        print(f"Received {len(data)} bytes of data from client.")

        os.makedirs(FILES_DIR, exist_ok=True)
        input_file = os.path.join(FILES_DIR, "received_audio.wav")
        with open(input_file, "wb") as f:
            f.write(data)

        print(f"Audio file saved to {input_file}")

        process_audio()

        output_file = os.path.join(FILES_DIR, "denoised_audio.wav")
        with open(output_file, "rb") as f:
            processed_data = f.read()

        writer.write(processed_data)
        await writer.drain()
        print("Processed audio sent back to client.")

    except Exception as e:
        print(f"Error: {e}")
    finally:
        writer.close()
        await writer.wait_closed()




@asynccontextmanager
async def startup_and_shutdown(app: FastAPI):
    task = asyncio.create_task(start_tcp_server())
    yield
    task.cancel()


app = FastAPI(lifespan=startup_and_shutdown)


async def start_tcp_server():
    server = await asyncio.start_server(handle_client, "0.0.0.0", 8888)
    addr = server.sockets[0].getsockname()
    print(f"Serving on {addr}")
    async with server:
        await server.serve_forever()

@app.get("/")
async def root():
    return {"message": "TCP server for text processing is running."}