export const Card = ({ children, className = "", hover = false, ...props }) => {
  return (
    <div
      className={`bg-white rounded-lg shadow-md overflow-hidden ${
        hover ? "hover:shadow-xl transition-shadow duration-300" : ""
      } ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardHeader = ({ children, className = "" }) => {
  return (
    <div className={`px-6 py-4 border-b border-primary-100 ${className}`}>
      {children}
    </div>
  );
};

export const CardBody = ({ children, className = "" }) => {
  return <div className={`px-6 py-4 ${className}`}>{children}</div>;
};

export const CardFooter = ({ children, className = "" }) => {
  return (
    <div className={`px-6 py-4 border-t border-primary-100 ${className}`}>
      {children}
    </div>
  );
};
