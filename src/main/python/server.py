from http.server import BaseHTTPRequestHandler, HTTPServer
import json
import os

class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):
    """
   A HTTP request handler for serving an HTML login page and handling authentication requests

   GET  /googleSignIN.html  -> Serves the Google Sign-In page.
   POST /auth               -> Receives and saves the authentication token.
   """
    def do_GET(self):
        if self.path == "/googleSignIN.html":
            try:
                # Get the directory
                script_dir = os.path.dirname(os.path.abspath(__file__))
                
                # Relative path to the HTML file
                relative_path = os.path.join(script_dir, "..", "resources", "public", "googleSignIN.html")
                
                # Normalize the path
                html_file_path = os.path.normpath(relative_path)
                
                # Read the HTML file
                with open(html_file_path, "rb") as file:
                    html_content = file.read().decode("utf-8")
                
                # Read config file to get the client ID
                config_path = os.path.join(script_dir, "..", "resources", "config.properties")
                config_path = os.path.normpath(config_path)
                client_id = ""
                
                try:
                    with open(config_path, "r") as config_file:
                        for line in config_file:
                            if line.startswith("GOOGLE_CLIENT_ID="):
                                client_id = line.split("=", 1)[1].strip()
                                break
                except FileNotFoundError:
                    print("Config file not found.")
                
                # Inject the client ID into the HTML
                if client_id:
                    # Replace placeholder or add script to call loadGoogleSignIn
                    script_tag = f"""
                    <script>
                        window.onload = function() {{
                            loadGoogleSignIn("{client_id}");
                        }}
                    </script>
                    </body>
                    """
                    html_content = html_content.replace("</body>", script_tag)
                
                self.send_response(200)
                self.send_header("Content-type", "text/html")
                self.send_header("Access-Control-Allow-Origin", "*")
                self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                self.send_header("Access-Control-Allow-Headers", "Content-Type")
                self.end_headers()
                self.wfile.write(html_content.encode("utf-8"))
            except FileNotFoundError:
                self.send_error(404, "File Not Found")
        else:
            self.send_error(404, "Endpoint Not Found")

    def do_POST(self):
        """
       Handles POST requests.

       If the request path is "/auth", it reads a JSON payload containing a Google authentication token,
       saves the token to a file (`token.txt`), and responds with a success message.
       Otherwise, it returns a 404 error.
       """

        if self.path == "/auth":
            content_length = int(self.headers["Content-Length"])
            post_data = self.rfile.read(content_length)
            token = json.loads(post_data.decode("utf-8"))["token"]
            print("Received Google Token:", token)

            with open("token.txt", "w") as file:   # Save the token to a file
                file.write(token)

            # Send a response back to the client
            self.send_response(200)   # 200 means OK
            self.send_header("Content-type", "application/json")
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
            self.send_header("Access-Control-Allow-Headers", "Content-Type")
            self.end_headers()
            response = json.dumps({"status": "success"})
            self.wfile.write(response.encode("utf-8"))
        else:
            self.send_error(404, "Endpoint Not Found")  # Send a 404 error if the endpoint is not recognized

    def do_OPTIONS(self):
        self.send_response(200)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.end_headers()

def run(server_class=HTTPServer, handler_class=SimpleHTTPRequestHandler, port=63347):
    """
  Starts the HTTP server.

  Args:
      server_class: The HTTP server class (default: HTTPServer).
      handler_class: The request handler class (default: SimpleHTTPRequestHandler).
      port: The port number to run the server on (default: 63347).
  """
    server_address = ("", port)
    httpd = server_class(server_address, handler_class)
    print(f"Starting server on port {port}...")
    httpd.serve_forever()

if __name__ == "__main__":
    run()