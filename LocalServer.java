import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class LocalServer {
    private static HttpServer server;
    
    public static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/paypal-return", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);
                
                String paymentId = params.get("paymentId");
                String payerId = params.get("PayerID");
                
                String response;
                if (paymentId != null && payerId != null) {
                    response = "<html><body style='font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-align: center; padding: 50px;'>"
                             + "<div style='background: rgba(255,255,255,0.1); padding: 30px; border-radius: 15px; backdrop-filter: blur(10px);'>"
                             + "<h2 style='color: #4CAF50;'>✅ Payment Completed Successfully!</h2>"
                             + "<p>Payment ID: " + paymentId + "</p>"
                             + "<p>Thank you for your purchase!</p>"
                             + "<button onclick='window.close()' style='background: #4CAF50; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; margin-top: 20px;'>Close Window</button>"
                             + "</div></body></html>";
                } else {
                    response = "<html><body style='font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-align: center; padding: 50px;'>"
                             + "<div style='background: rgba(255,255,255,0.1); padding: 30px; border-radius: 15px; backdrop-filter: blur(10px);'>"
                             + "<h2>Payment completed successfully!</h2>"
                             + "<button onclick='window.close()' style='background: #4CAF50; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; margin-top: 20px;'>Close Window</button>"
                             + "</div></body></html>";
                }
                
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/paypal-cancel", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "<html><body style='font-family: Arial, sans-serif; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%); color: white; text-align: center; padding: 50px;'>"
                               + "<div style='background: rgba(255,255,255,0.1); padding: 30px; border-radius: 15px; backdrop-filter: blur(10px);'>"
                               + "<h2>❌ Payment Cancelled!</h2>"
                               + "<p>Your payment was cancelled. You can try again anytime.</p>"
                               + "<button onclick='window.close()' style='background: #ff6b6b; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; margin-top: 20px;'>Close Window</button>"
                               + "</div></body></html>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("✅ LocalServer running at http://localhost:8000");
        System.out.println("📋 Endpoints:");
        System.out.println("   - http://localhost:8000/paypal-return");
        System.out.println("   - http://localhost:8000/paypal-cancel");
    }
    
    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("🛑 LocalServer stopped");
        }
    }
    
    private static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }
            }
        }
        return result;
    }
}