export const formatRuntime = (minutes) => {
    const n = Number(minutes);
    if (!Number.isFinite(n) || n <= 0) return null;
    const h = Math.floor(n / 60);
    const m = Math.round(n % 60);
    if (h === 0) return `${m}m`;
    return m === 0 ? `${h}h` : `${h}h ${m}m`;
};

export const getYear = (releaseDate) => {
    if (!releaseDate) return null;
    const year = new Date(releaseDate).getFullYear();
    return Number.isFinite(year) && year > 1800 ? year : null;
};

export const formatRating = (voteAverage) => {
    const n = Number(voteAverage);
    if (!Number.isFinite(n) || n <= 0) return null;
    return n.toFixed(1);
};

export const formatVoteCount = (voteCount) => {
    const n = Number(voteCount);
    if (!Number.isFinite(n) || n <= 0) return null;
    if (n >= 1_000_000) return `${(n / 1_000_000).toFixed(1)}M`;
    if (n >= 1_000) return `${(n / 1_000).toFixed(n >= 10_000 ? 0 : 1)}K`;
    return String(n);
};

export const truncate = (text, max = 220) => {
    if (!text || typeof text !== "string") return "";
    const clean = text.trim().replace(/\s+/g, " ");
    if (clean.length <= max) return clean;
    const cut = clean.slice(0, max);
    const lastSpace = cut.lastIndexOf(" ");
    return `${cut.slice(0, lastSpace > max * 0.6 ? lastSpace : max).replace(/[,;:.\-–—\s]+$/, "")}…`;
};

// genres may arrive as [{ id, name }] or as plain strings.
export const getGenreNames = (genres, limit = 3) => {
    if (!Array.isArray(genres)) return [];
    return genres
        .map((g) => (typeof g === "string" ? g : g?.name))
        .filter(Boolean)
        .slice(0, limit);
};

export const getBackdrop = (movie) =>
    movie?.backdrops?.[0] || movie?.backdropPath || "";

/**
 * Classifies a trailer URL.
 * Returns { kind: "iframe" | "video" | "external", src, href } or null.
 * - YouTube (watch, youtu.be, embed, shorts) and Vimeo -> iframe embed
 * - direct .mp4/.webm/.ogg files -> <video>
 * - any other http(s) URL -> opened in a new tab
 */
export const getTrailerSource = (url) => {
    if (!url || typeof url !== "string") return null;
    let u;
    try {
        u = new URL(url.trim());
    } catch {
        return null;
    }
    if (u.protocol !== "https:" && u.protocol !== "http:") return null;

    const host = u.hostname.replace(/^www\.|^m\./, "");
    const isYouTubeId = (id) => /^[\w-]{11}$/.test(id || "");

    let ytId = null;
    if (host === "youtu.be") {
        ytId = u.pathname.slice(1).split("/")[0];
    } else if (host === "youtube.com" || host === "youtube-nocookie.com") {
        if (u.pathname === "/watch") ytId = u.searchParams.get("v");
        else {
            const m = u.pathname.match(/^\/(?:embed|shorts|v|live)\/([\w-]{11})/);
            ytId = m?.[1] ?? null;
        }
    }
    if (isYouTubeId(ytId)) {
        return {
            kind: "iframe",
            src: `https://www.youtube-nocookie.com/embed/${ytId}?autoplay=1&rel=0&modestbranding=1`,
            href: `https://www.youtube.com/watch?v=${ytId}`,
        };
    }

    if (host === "vimeo.com" || host === "player.vimeo.com") {
        const m = u.pathname.match(/(\d{6,})/);
        if (m) {
            return {
                kind: "iframe",
                src: `https://player.vimeo.com/video/${m[1]}?autoplay=1`,
                href: u.href,
            };
        }
    }

    if (/\.(mp4|webm|ogg)$/i.test(u.pathname)) {
        return { kind: "video", src: u.href, href: u.href };
    }

    return { kind: "external", src: u.href, href: u.href };
};