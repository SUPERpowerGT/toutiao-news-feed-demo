# App Demo Rehearsal Evidence - 8 August 2026

## Environment

- Android package: `com.xuziyi.toutiaoandroid`
- Emulator backend URL: `http://10.0.2.2:8080`
- Runtime: Docker Compose backend plus PostgreSQL
- Evidence screenshots: E35-E42

## Verified Sequence

1. Rebuilt the backend image and reset deterministic seed data; `/health` returned `ok`.
2. Opened Recommend and Video scenes in the installed Android app.
3. Opened a video detail. The embedded backend media endpoint returned `video/mp4`, and a real frame and playback timer were visible in E37.
4. Appended five newer records through `/seed/append?count=5`, pulled to refresh, and received HTTP 200 for the `refresh_time` request.
5. Refreshed again without appending; the second `refresh_time` request returned HTTP 200 without duplicate insertion.
6. Cleared app data for a cold network-backed pagination run. Two successive requests used cursors `1786159159` and `1786146559`; both returned HTTP 200.
7. Warmed the Room cache, stopped the backend, force-stopped and relaunched the app. E40 shows the recommendation feed remained available from Room.
8. Opened an uncached detail while the backend was stopped. E41 shows the connection error and Retry action.
9. Started the backend and pressed Retry without restarting the app. E42 shows the recovered detail.

## Media Endpoint Verification

- Route: `/media/demo-video.mp4`
- Content type: `video/mp4`
- Content length: `475085`
- Range support: `Accept-Ranges: bytes`
- Automated tests: full response and `bytes=0-99` partial response

## Recording Boundary

These records prove that the rehearsal states were reached. The final five-minute video must still show the gestures and stop/start commands continuously; screenshots are supporting evidence, not a substitute for the live demonstration.
