const ProfileStats = ({ profile }) => {
    const stats = [
        {
            label: "Avg Rating",
            value: profile.avgRating != null ? profile.avgRating.toFixed(1) : "-",
        },
        { label: "Ratings", value: profile.ratingsCount },
        { label: "Reviews", value: profile.reviewsCount },
        { label: "Watched", value: profile.moviesWatchedCount },
        { label: "To Watch", value: profile.moviesToWatchCount },
    ];

    return (
        <div className="profile-stats-grid">
            {stats.map((stat) => (
                <div className="profile-stat-card" key={stat.label}>
                    <span className="profile-stat-value">{stat.value}</span>
                    <span className="profile-stat-label">{stat.label}</span>
                </div>
            ))}
        </div>
    );
};

export default ProfileStats;