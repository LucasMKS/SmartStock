const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

if (!API_BASE_URL) {
  throw new Error(
    "NEXT_PUBLIC_API_BASE_URL is not defined in your .env.local file."
  );
}

// Função utilitária para lidar com respostas da API
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorData = await response
      .json()
      .catch(() => ({ message: response.statusText }));
    throw new Error(errorData.message || "Ocorreu um erro na API.");
  }
  return response.json();
}

// Função genérica para requisições GET
export async function get<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`);
  return handleResponse(response);
}

// Função genérica para requisições POST
export async function post<T>(path: string, body: any): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });
  return handleResponse(response);
}

// Função genérica para requisições PUT
export async function put<T>(path: string, body: any): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });
  return handleResponse(response);
}

// Função genérica para requisições DELETE
export async function del<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
  });
  return handleResponse(response);
}
