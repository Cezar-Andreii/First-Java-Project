
# Weather App

Weather App este o aplicație client-server care furnizează prognoze meteo pentru diverse locații utilizând un set de date predefinit. Aplicația permite utilizatorilor să caute date meteo pe baza coordonatelor geografice și să gestioneze aceste date (în modul administrator).

## Caracteristici
- **Acces pentru utilizatori obișnuiți (regular):** Caută prognoza meteo pentru locațiile cele mai apropiate de coordonatele geografice oferite.
- **Mod administrator (admin):** Permite adăugarea de noi locații și actualizarea bazei de date cu prognozele meteo.
- **Structură extensibilă:** Cod modular cu clase dedicate pentru date meteo, prognoze și gestionarea datelor JSON.
- **Comunicație client-server prin socket-uri.**

## Structura proiectului
- **`Server`**: Gestionează cererile utilizatorilor și stochează baza de date meteo.
- **`Client`**: Se conectează la server pentru a trimite cereri și a primi răspunsuri.
- **`WeatherData`**: Reprezintă o locație geografică și prognoza aferentă.
- **`WeatherForecast`**: Stochează condițiile meteo și temperatura pentru o zi.
- **Date JSON**: Conține informațiile meteo pentru locațiile disponibile.
