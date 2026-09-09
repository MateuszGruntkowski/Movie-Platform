export const getYoutubeId = (url) => {
    try {
        const u = new URL(url);
        if (u.hostname.includes("youtu.be")) return u.pathname.slice(1);
        return u.searchParams.get("v");
    } catch {
        return null;
    }
};

export const formatCurrency = (amount) => {
    if (!amount) return null;
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        notation: "compact",
        maximumFractionDigits: 1,
    }).format(amount);
};

export const formatLanguage = (code) => {
    if (!code) return null;
    try {
        const displayNames = new Intl.DisplayNames(["en"], { type: "language" });
        const name = displayNames.of(code);
        return name ? name.charAt(0).toUpperCase() + name.slice(1) : code.toUpperCase();
    } catch {
        return code.toUpperCase();
    }
};

export const formatReleaseDate = (releaseDate) => {
    if (!releaseDate) return null;
    try {
        return new Date(releaseDate).getFullYear();
    } catch {
        return releaseDate;
    }
};

export const formatRuntime = (runtime) => {
    if (!runtime) return null;
    const hours = Math.floor(runtime / 60);
    const minutes = runtime % 60;
    return `${hours}h ${minutes}m`;
};