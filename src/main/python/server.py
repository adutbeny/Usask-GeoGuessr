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
        """
       Handles GET requests.

       If the requested path is "/googleSignIN.html", it serves the corresponding HTML file.
       Otherwise, it returns a 404 error.
       """

        if self.path == "/googleSignIN.html":
            try:
                # get the dir
                script_dir = os.path.dirname(os.path.abspath(__file__))

                #  relative path to the HTML file
                relative_path = os.path.join(script_dir, "..", "resources", "public", "googleSignIN.html")

                # mormalize the path
                html_file_path = os.path.normpath(relative_path)

                #  read the HTML file
                with open(html_file_path, "rb") as file:
                    self.send_response(200)
                    self.send_header("Content-type", "text/html")
                    self.end_headers()
                    self.wfile.write(file.read())
            except FileNotFoundError:
                self.send_error(404, "File Not Found")   # Send a 404 error if the file is not found
        else:
            self.send_error(404, "Endpoint Not Found")   # Send a 404 error if the endpoint is not recognized

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
            self.end_headers()
            response = json.dumps({"status": "success"})
            self.wfile.write(response.encode("utf-8"))
        else:
            self.send_error(404, "Endpoint Not Found")  # Send a 404 error if the endpoint is not recognized

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