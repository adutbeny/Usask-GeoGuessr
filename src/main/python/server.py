from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import os

class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # Serve the HTML file for GET requests
        if self.path == "/googleSignIN.html":
            try:
                # Absolute path to the HTML file
                html_file_path = "C:\\Users\\Ben\\Desktop\\cmpt_370\\src\\main\\resources\\public\\googleSignIN.html"

                # Open and read the HTML file
                with open(html_file_path, "rb") as file:
                    self.send_response(200)
                    self.send_header("Content-type", "text/html")
                    self.end_headers()
                    self.wfile.write(file.read())
            except FileNotFoundError:
                self.send_error(404, "File Not Found")
        else:
            self.send_error(404, "Endpoint Not Found")

    def do_POST(self):
        # Handle POST requests (e.g., /auth)
        if self.path == "/auth":
            content_length = int(self.headers["Content-Length"])
            post_data = self.rfile.read(content_length)
            token = json.loads(post_data.decode("utf-8"))["token"]
            print("Received Google Token:", token)

            # Write the token to a file
            with open("token.txt", "w") as file:
                file.write(token)

            # Send a response back to the client
            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            response = json.dumps({"status": "success"})
            self.wfile.write(response.encode("utf-8"))
        else:
            self.send_error(404, "Endpoint Not Found")

def run(server_class=HTTPServer, handler_class=SimpleHTTPRequestHandler, port=63347):
    server_address = ("", port)
    httpd = server_class(server_address, handler_class)
    print(f"Starting server on port {port}...")
    httpd.serve_forever()

if __name__ == "__main__":
    run()