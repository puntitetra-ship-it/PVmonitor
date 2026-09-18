# PV Monitor V3

Android dashboard for a local home-energy setup:

- Zendure SolarFlow 2400 AC+ via local zenSDK `GET /properties/report`
- Shelly Pro 3EM via local RPC `GET /rpc/EM.GetStatus?id=0`
- Enphase IQ Gateway via local `/production.json`
- 5-second refresh
- PV production, house load, grid import/export, battery power and SoC
- Device IPs and optional Enphase bearer token are saved locally on the phone
- Read-only: this version does not send battery control commands

## Start

1. Open this folder in Android Studio.
2. Let Gradle sync.
3. Run on an Android phone connected to the same LAN as the devices.
4. Open **Einstellungen** and enter the local IP addresses.
5. For newer Enphase Gateway firmware, paste a valid local bearer token if required.

## Sign conventions

Shelly is expected at the grid connection point:
- positive total active power = grid import
- negative total active power = export

Battery:
- positive = charging
- negative = discharging

House load is calculated as an approximate energy balance. If your CT direction or installation differs,
the sign convention can be adjusted in `MainActivity.kt`.

## Security note

The Enphase section accepts a local/self-signed Gateway TLS certificate so it can communicate on the LAN.
Do not expose this app or these device endpoints directly to the public internet.
