import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.text.DecimalFormat;

public class ConversorDeMoedas {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/ebf7e477e6ae02d81e85d6a2/latest/USD";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao Conversor de Moedas!");

        while (true) {
            int escolha = exibirMenu(scanner);
            if (escolha == 11) {
                System.out.println("Saindo...");
                break;
            }

            System.out.println("Digite o valor:");
            double valor = scanner.nextDouble();

            double taxaDeConversao = obterTaxaDeConversao(escolha);
            if (taxaDeConversao != 0) {
                double resultado = calcularConversao(valor, taxaDeConversao);
                System.out.println("Resultado: " + formatarResultado(resultado));
            } else {
                System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }

    private static int exibirMenu(Scanner scanner) {
        System.out.println("Selecione a conversão desejada:");
        System.out.println("1. USD para BRL");
        System.out.println("2. BRL para USD");
        System.out.println("3. USD para ARS");
        System.out.println("4. ARS para USD");
        System.out.println("5. USD para BOB");
        System.out.println("6. BOB para USD");
        System.out.println("7. USD para CLP");
        System.out.println("8. CLP para USD");
        System.out.println("9. USD para COP");
        System.out.println("10. COP para USD");
        System.out.println("11. Sair");
        return scanner.nextInt();
    }

    private static double obterTaxaDeConversao(int escolha) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error code : " + response.statusCode());
            }

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject rates = jsonResponse.getAsJsonObject("conversion_rates");

            return obterTaxaPelaEscolha(escolha, rates);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static double obterTaxaPelaEscolha(int escolha, JsonObject rates) {
        switch (escolha) {
            case 1: return rates.get("BRL").getAsDouble();
            case 2: return 1 / rates.get("BRL").getAsDouble();
            case 3: return rates.get("ARS").getAsDouble();
            case 4: return 1 / rates.get("ARS").getAsDouble();
            case 5: return rates.get("BOB").getAsDouble();
            case 6: return 1 / rates.get("BOB").getAsDouble();
            case 7: return rates.get("CLP").getAsDouble();
            case 8: return 1 / rates.get("CLP").getAsDouble();
            case 9: return rates.get("COP").getAsDouble();
            case 10: return 1 / rates.get("COP").getAsDouble();
            default: return 0;
        }
    }

    private static double calcularConversao(double valor, double taxaDeConversao) {
        return valor * taxaDeConversao;

    }
    private static String formatarResultado(double resultado) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(resultado);
    }

}
