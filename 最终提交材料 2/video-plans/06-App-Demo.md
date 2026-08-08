# Video 6 - Presentation Assessment App Demo

## Delivery Information

- Required filename: `TeamXX- Presentation Assessment App Demo.mp4`
- Maximum duration: 5 minutes
- Primary purpose: demonstrate a real running application and complete core user flow.

## Demo Environment

- Android emulator at 1920x1080 recording resolution or a clearly readable equivalent.
- Docker Compose backend and PostgreSQL healthy.
- Backend available to the emulator at `http://10.0.2.2:8080`.
- Seed data reset before recording.
- Notification popups and unrelated applications disabled.

## Required Demo Flow

- App launch and first-screen loading/skeleton state.
- Recommendation feed with official content and mixed card types.
- Recommendation reason display.
- Switch between recommend, video, Shenzhen, technology, sports, and finance scenes.
- Pull to refresh after adding fresh seed data.
- Pull to refresh when no newer content exists.
- Cursor-based load more and footer state.
- Open a video item and show the real detail content and automatic playback.
- Relaunch with the backend stopped and show Room cache fallback.
- Controlled backend-unavailable error and retry recovery.

> Current scope note: Room cache fallback, news detail navigation, and basic lifecycle-aware video playback are implemented. Advanced cache expiration and optimised short-video streaming remain future work.

## Preparation Checklist

- [ ] Run `docker compose -f docker-compose.prod.yml up --build -d`.
- [ ] Confirm `/health` returns `ok`.
- [ ] Reset data through `/seed`.
- [ ] Launch the app and wait for initial image caching before the final take.
- [ ] Prepare `scripts/append_refresh_data.sh` in a visible terminal.
- [ ] Confirm all required channels contain suitable data.
- [ ] Confirm refresh and load-more work twice consecutively.
- [ ] Prepare backend stop/start commands for the controlled recovery scene.
- [ ] Prepare a backup recording of the complete flow.

## Suggested Timeline

| Time | Action | Narration |
|---|---|---|
| 0:00-0:25 | Launch app | Introduce the full-stack demo and main user goal |
| 0:25-1:05 | Initial feed | Skeleton, official items, cards, recommendation reasons |
| 1:05-1:50 | Change scenes | Explain isolated scene data and rendering |
| 1:50-2:40 | Append data and refresh | Show new-content refresh and update banner |
| 2:40-3:10 | Refresh again | Show no-new-content state |
| 3:10-3:50 | Scroll and load more | Show cursor pagination and footer behavior |
| 3:50-4:15 | Recommendation evidence | Highlight recommendation reasons and mixed cards |
| 4:15-4:50 | Error and recovery | Stop backend, show the error state, restart, and retry |
| 4:50-5:00 | Close | Summarize the demonstrated end-to-end path |

## Exact Recording Runbook

### 1. Preflight Before Recording

Run these commands from the repository root before opening the recording software:

```bash
docker compose -f docker-compose.prod.yml up --build -d
curl -fsS http://localhost:8080/health
curl -fsS http://localhost:8080/seed/channels
adb devices
```

Expected results:

- The health endpoint prints `ok`.
- The channel endpoint reports success. Returning `added 0` is valid when data is already complete.
- `adb devices` shows one emulator with status `device`, not `offline`.
- The emulator can load `http://10.0.2.2:8080` through the Android application.

Prepare three terminal commands in advance, but do not execute them yet:

```bash
./scripts/append_refresh_data.sh 5
docker compose -f docker-compose.prod.yml stop backend
docker compose -f docker-compose.prod.yml start backend
```

Before the final take, disable desktop and emulator notifications, close private windows, warm the image cache once, return the feed to the top, and force-stop the app so the recording starts from a clean launch.

### 2. Exact Five-Minute Script

| Time | Operator Action | English Narration |
|---|---|---|
| 0:00-0:15 | Show the title slide with project name and speaker name. | "Hello, this is the live application demonstration of our Toutiao-style news feed system. I will demonstrate the real connection between the Android client, Go backend, and PostgreSQL database." |
| 0:15-0:35 | Launch the Android app and keep the skeleton screen visible briefly. | "When the application starts, it requests the initial feed from the backend. A skeleton layout provides immediate visual feedback while the network request is in progress." |
| 0:35-1:05 | Pause on Recommend. Point out official cards, image/text/video cards, statistics, and recommendation reasons. | "The Recommend channel combines official top stories with normal ranked content. The client maps each content type to a dedicated Compose card, while recommendation reasons and engagement statistics come from the backend response." |
| 1:05-1:30 | Swipe through Video, Shenzhen, Technology, Sports, and Finance. | "Each channel sends an independent scene key. The backend applies content-type, city, or category filters, and every visible channel contains at least twenty records." |
| 1:30-1:50 | Open a video card, show the loaded article body and automatic playback, then go back. | "Card navigation carries the news identifier to a real detail API. Video content starts after preparation and pauses or releases with the screen lifecycle." |
| 1:50-2:05 | Run `./scripts/append_refresh_data.sh 5`, then return to the emulator. | "I am appending five newer server records rather than changing the UI locally." |
| 2:05-2:35 | Pull to refresh, show the update banner, then refresh once more without appending. | "Refresh requests only newer timestamps and merges unique IDs. A second refresh returns no duplicates when the server has no newer content." |
| 2:35-3:05 | Scroll until the loading footer appears and more cards are appended. | "Near the list end, cursor pagination loads the next page while preserving visible content." |
| 3:05-3:20 | Run `docker compose -f docker-compose.prod.yml stop backend`, then force-stop and relaunch the app. | "I am stopping the backend and relaunching the client to test offline availability." |
| 3:20-3:50 | Show the recommendation feed restored from Room. | "The network request now fails, but the repository falls back to scene-filtered Room data, so the cached feed remains available." |
| 3:50-4:15 | Open a card while backend remains stopped and show the detail error with Retry. | "The uncached detail request becomes an explicit error state. The application does not crash and provides a retry action." |
| 4:15-4:35 | Start the backend and press Retry in the detail screen. | "After restoring the backend, Retry repeats the same request without restarting the application." |
| 4:35-4:55 | Show the recovered detail and return to the feed. | "The detail and feed recover successfully, proving the Android, Room, API, and PostgreSQL paths." |
| 4:55-5:00 | Return to the title or keep the recovered app visible. | "This concludes the live demonstration of channel browsing, refresh, pagination, and error recovery." |

### 3. Recording Controls

- Keep the emulator large enough that card titles and state messages are readable.
- Show the terminal only for the append, stop, and start commands.
- Cut command waiting time, but do not replace successful application interactions with screenshots.
- If images load slowly, wait for caching before restarting the final take.
- If the error does not appear after stopping the backend, switch to a channel that has not been opened during that take.
- If five minutes is tight, omit the optional five-channel sweep; do not omit refresh, pagination, or recovery.

### 4. Post-Recording Verification

```bash
docker compose -f docker-compose.prod.yml start backend
curl -fsS http://localhost:8080/health
```

Export as `TeamXX- Presentation Assessment App Demo.mp4`, verify 1920x1080 resolution, play the file from beginning to end, and retain one backup take.

## Recording Notes

- Record interactions continuously; avoid replacing the running app with screenshots.
- Keep a small terminal visible only when it helps prove backend data changes.
- Demonstrate real detail and basic playback, but do not claim advanced streaming or preloading.
- If a scenario is unstable, fix it before recording rather than explaining around it.

## Definition of Done

- [ ] Video is five minutes or shorter.
- [ ] App, backend, and database are genuinely connected.
- [ ] Initial load, channels, refresh, pagination, and error recovery are shown.
- [ ] No passwords or private desktop content appear.
- [ ] A backup take exists.
