import subprocess
import os
import sys

def run_react_frontend():
    project_path = r"C:\Users\Edwin Nyaungwa\Desktop\projects\James-Beds\Frontend"


    os.chdir(project_path)

    print("📦 Installing dependencies...")
    subprocess.run(["npm", "install"], shell=True)

    print("🚀 Starting Vite development server...")
    print("Your app will open at: http://localhost:5173")

    # Run: npm run dev
    subprocess.run(["npm", "run", "dev"], shell=True)

if __name__ == "__main__":
    try:
        run_react_frontend()
    except KeyboardInterrupt:
        print("\n🛑 Server stopped manually.")
        sys.exit(0)
