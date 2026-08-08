# Video 5 - Value Added Assessment

## Delivery Information

- Required filename: `TeamXX- Value Added Assessment.mp4`
- Target duration: 5 minutes
- Primary purpose: prove measurable value beyond a basic static feed demo.

## Recommended Value Story

Use one connected story instead of listing future technologies:

**Explainable multi-channel recommendation plus measurable backend resilience.**

The implemented system provides scene-aware recommendation scores and reasons, official-content handling, multi-channel separation, refresh/load-more behavior, and a verified database connection-pool performance improvement.

## Required Content

- Baseline problem and why a static feed is insufficient.
- Implemented recommendation signals: weight, freshness, engagement, official boost, and scene boost.
- Explainable recommendation reasons shown to the client.
- Multi-channel behavior for recommend, video, Shenzhen, technology, sports, and finance.
- Technical differentiation from the minimum feed requirement.
- Measured performance before and after the connection-pool fix.
- Business or user value created by the implementation.
- Sponsor/mentor acceptance or feedback.
- Limitations and next measurable experiment.

## Evidence Required Before Recording

- [x] Recommendation score implementation and tests.
- [x] Recommendation reason implementation and API response.
- [x] App screenshots showing different channels and reasons.
- [x] Integration test result for recommend and video scenes.
- [x] Before result: one request completed followed by connection-pool timeout.
- [x] After result: 500 requests, 25 concurrency, zero failures.
- [x] Throughput and P95/P99 metrics.
- [x] Sponsor/mentor status documented transparently: formal acceptance was unavailable, so no acceptance claim is made.

## App Evidence Screenshots

These screenshots were captured from the installed Debug APK on the Android emulator at its native 1080x2400 resolution.

| Recommend: official Top content and explainable reason | Following: isolated creator feed |
|---|---|
| ![Recommend channel](../assets/E13-value-recommend.png) | ![Following channel](../assets/E14-value-following.png) |

| Hot: scene-specific ranking | Video: video cards, covers, play action, and duration |
|---|---|
| ![Hot channel](../assets/E15-value-hot.png) | ![Video channel](../assets/E16-value-video.png) |

| Image: content-type filtering and image-card rendering |
|---|
| ![Image channel](../assets/E17-value-image.png) |

Recommended recording use: show the Recommend screenshot first, then use the Following/Hot pair to prove scene separation, and finish with the Video/Image pair to prove content-type-specific rendering. Keep the complete screenshot sequence within 35 to 40 seconds.

## Exact Recording Runbook and Oral Script

Prepare the recommendation implementation, the five screenshots above, and `evidence/verification-2026-08-08.md` before recording. Read the following English script at a steady pace and change the screen when indicated.

| Time | Screen | Exact English Narration |
|---|---|---|
| 0:00-0:35 | Title slide, then Recommend screenshot | "This video assesses the additional value delivered by our news feed application. A basic feed could simply display a static list of articles. Our implementation goes further by combining explainable multi-channel ranking, content-specific presentation, cache-first mobile behavior, and measured backend resilience. The value hypothesis is that users can find relevant content more quickly, understand why key stories are surfaced, and continue using previously loaded content when the network is unstable." |
| 0:35-1:25 | Open `backend/application/feed_service.go` and highlight the score calculation | "The first value-adding capability is the recommendation service. It uses five transparent signals. Content weight contributes forty percent, freshness contributes thirty percent, and engagement contributes twenty percent. Engagement combines likes, comments, shares, and favourites, then applies logarithmic normalisation so that a single large count does not dominate the ranking. Official and top stories receive a small authority boost, while content matching the requested scene receives a scene boost. The final score is calculated in the application service and covered by automated tests. This is deliberately a rule-based and interpretable ranking model, not a trained machine-learning model, so every result can be explained and reproduced." |
| 1:25-2:00 | Show the Recommend screenshot and, if convenient, the recommendation-reason code or API field | "The ranking also returns a human-readable recommendation reason. Examples include authoritative release, official media recommendation, popular discussion, latest update, and channel-specific reasons. On the Recommend screen, the top stories are clearly labelled with an authoritative-release reason. This makes the result more understandable than an unexplained score and gives the client a consistent field that can be displayed directly in the interface." |
| 2:00-2:50 | Show Following, Hot, Video, and Image screenshots in that order | "The second capability is genuine multi-channel separation. The application supports eleven independent channel views, including Recommend, Following, Hot, Shenzhen, Video, Featured, Image, War, Sports, Finance, and Technology. Our deterministic dataset contains at least twenty records for every channel. Following and Hot show different ranked feeds. The Video channel renders large video covers, a play action, and duration metadata, while the Image channel applies content-type filtering and image-card presentation. Local and category channels use the same API contract with their own scene filters. These are not duplicated screenshots of one list; they represent separate query and rendering behaviour." |
| 2:50-3:25 | Briefly demonstrate refresh, open an article detail, and return to the feed | "The mobile client adds practical user value beyond presentation. Feed data is stored in Room and loaded cache-first, so previously fetched stories can appear without waiting for the network. A background request refreshes the local cache when connectivity is available. Pull-to-refresh and load-more update that cache, and selecting an item opens a dedicated detail route backed by the news detail API. Video items also use lifecycle-aware playback. Together, these features turn the project from a static interface mock-up into a usable end-to-end application flow." |
| 3:25-4:25 | Open `evidence/verification-2026-08-08.md` and show the before/after table | "The third area of value is measurable backend resilience. During stress testing, the original implementation exhausted a ten-connection database pool. It kept outer query rows open while nested media queries attempted to acquire additional connections. As a result, only one request completed before the test timed out. We corrected the query lifecycle by reading and closing the outer rows before loading related media. After the fix, ApacheBench completed five hundred requests at concurrency twenty-five with zero failed requests. Throughput reached one thousand two hundred and sixty-seven point seven six requests per second. Mean latency was nineteen point seven two milliseconds, P ninety-five was thirty-five milliseconds, P ninety-nine was forty milliseconds, and maximum latency was forty-five milliseconds. A second run again produced zero failures, with more than one thousand one hundred requests per second and P ninety-nine below sixty milliseconds. This repeat run shows that the improvement was reproducible rather than a single favourable result." |
| 4:25-4:48 | Return to the Recommend and Video screenshots | "These delivered capabilities create value in two ways. Users receive faster, clearer navigation across content types and more understandable recommendations. From an engineering perspective, deterministic ranking, cached mobile data, automated tests, and repeatable load results make the system easier to validate and maintain. Formal sponsor or mentor acceptance was not available at the time of submission, so no acceptance claim is made in this assessment." |
| 4:48-5:00 | Closing slide with limitation and next experiment | "The main limitation is that ranking is not yet personalised from individual behaviour. The next measurable experiment would add consented interaction signals and compare click-through rate, feed latency, and retention against the current rule-based baseline. This separates delivered value from future work. Thank you." |

Recording control: keep the performance table visible while reading every metric, do not skip the sponsor-status sentence, and avoid presenting the final experiment as an implemented feature.

## Suggested Timeline

| Time | Narration | Screen Evidence |
|---|---|---|
| 0:00-0:35 | Baseline problem and value hypothesis | Static feed versus explainable multi-channel feed |
| 0:35-1:35 | Recommendation design | Signal and score explanation |
| 1:35-2:15 | Explainability | Reason field in API and app UI |
| 2:15-3:00 | Multi-channel value | Recommend/video/local/category demonstrations |
| 3:00-4:05 | Performance value | Connection-pool root cause and before/after metrics |
| 4:05-4:40 | Sponsor/user value | Acceptance or feedback evidence |
| 4:40-5:00 | Limitation and next experiment | Personalization/analytics roadmap |

## Recording Notes

- Do not present future AI, ML, analytics, or real-time ideas as implemented value.
- Focus on implemented behavior and measurable outcomes.
- Explain that the ranking is rule-based and interpretable, not a trained ML model.
- If sponsor feedback is unavailable, state that limitation explicitly instead of inventing acceptance.

## Definition of Done

- [ ] One clear value proposition connects all sections.
- [ ] Implementation and test evidence are visible.
- [ ] Before/after metrics are included.
- [ ] Sponsor status is accurate.
- [ ] Future ideas are clearly separated from delivered value.
