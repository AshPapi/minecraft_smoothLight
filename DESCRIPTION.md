# Smooth Light

**Smooth Light makes every light change in the world gradual instead of instant.**

Vanilla Minecraft switches lighting in a single frame: break a torch and the area goes dark immediately, place one and it's instantly bright. Smooth Light replaces these jumps with short, smooth transitions.

## Examples

- **Breaking a light source** — the light fades out smoothly
- **Placing a light source** — the light ramps up smoothly
- **Covering a light source or an opening with a block** — the light dims gradually
- **Opening a passage near a light source** — the light flows in smoothly

## How it works

No new blocks, no fake light sources and no client/server desync. The mod tracks per-position transitions of light emission and opacity directly inside the light engine, on both the client and the server, stepping them towards their target values over a few ticks. The final lighting is always exactly what vanilla would produce.

## Compatibility

- Works in singleplayer and on servers
- Affects all light sources automatically, **including modded ones**. Anything that emits light gets smooth transitions

The whole effect takes about a third of a second: fast enough to never get in the way, slow enough that your eyes notice the difference.

---

# Smooth Light (RU)

**Smooth Light делает все изменения света в мире плавными вместо мгновенных.**

Ванильный Minecraft переключает освещение за один кадр: если сломал факел, то вокруг мгновенно темнеет, поставил — мгновенно светло. Smooth Light заменяет эти скачки короткими плавными переходами.

## Примеры

- **Сломал источник света** — свет плавно гаснет
- **Поставил источник света** — свет плавно разгорается
- **Закрыл источник света или проём блоком** — свет тускнеет постепенно
- **Открыл проход рядом с источником** — свет плавно «вливается»

## Как это работает

Никаких новых блоков, фейковых источников света и рассинхрона клиент/сервер. Мод ведёт по-позиционные переходы эмиссии и светопроницаемости прямо в движке света, и на клиенте и на сервере, пошагово приближая их к целевым значениям за несколько тиков. Итоговое освещение всегда в точности совпадает с ванильным.

## Совместимость

- Работает в одиночной игре и на серверах
- Автоматически действует на все источники света, **включая модовые**. Всё, что излучает свет, получает плавные переходы

Весь эффект занимает около трети секунды: достаточно быстро, чтобы не мешать, и достаточно медленно, чтобы глаз заметил разницу.
