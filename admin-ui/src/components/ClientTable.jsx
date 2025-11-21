export default function ClientTable({ clients }) {
  return (
    <table className="w-full border text-sm">
      <thead className="bg-gray-100">
        <tr>
          <th className="p-2 border">Client ID</th>
        </tr>
      </thead>
      <tbody>
        {clients.map((c) => (
          <tr key={c.clientId}>
            <td className="p-2 border">{c.clientId}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}