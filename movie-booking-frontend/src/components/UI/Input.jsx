export const Input = ({
  label,
  error,
  className = "",
  icon: Icon,
  ...props
}) => {
  return (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-primary-700 mb-2">
          {label}
        </label>
      )}
      <div className="relative">
        {Icon && (
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Icon className="h-5 w-5 text-primary-400" />
          </div>
        )}
        <input
          className={`
            w-full px-4 py-2 border border-primary-300 rounded-lg
            focus:ring-2 focus:ring-accent focus:border-transparent
            disabled:bg-primary-50 disabled:cursor-not-allowed
            ${Icon ? "pl-10" : ""}
            ${error ? "border-red-500 focus:ring-red-500" : ""}
            ${className}
          `}
          {...props}
        />
      </div>
      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  );
};
