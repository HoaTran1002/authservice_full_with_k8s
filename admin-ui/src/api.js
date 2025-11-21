const API = "http://localhost:9000/api/admin/clients";

export const getClients = async () => {
  const res = await fetch(API);
  return res.json();
};

export const createClient = async (payload) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  return res.json();
};