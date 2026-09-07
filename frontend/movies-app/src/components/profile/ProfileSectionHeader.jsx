const ProfileSectionHeader = ({ title, sortValue, onSortChange, sortOptions }) => (
    <div className="profile-section-header">
        <h2 className="profile-section-title">{title}</h2>
        <select
            className="profile-sort-select"
            value={sortValue}
            onChange={(e) => onSortChange(e.target.value)}
        >
            {sortOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                    {opt.label}
                </option>
            ))}
        </select>
    </div>
);

export default ProfileSectionHeader;