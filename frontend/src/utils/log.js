const DEFAULT_ENDPOINT = process.env.REACT_APP_LOG_ENDPOINT || 'http://20.244.56.144/evaluation-service/logs';

const ALLOWED_STACK = ['backend', 'frontend'];
const ALLOWED_LEVEL = ['debug', 'info', 'warn', 'error', 'fatal'];
//implemetation of middleware in js
function normalizeAndValidate({ stack, level, pkg, message }) {
  const normalized = {
    stack: String(stack || '').trim().toLowerCase(),
    level: String(level || '').trim().toLowerCase(),
    package: String(pkg || '').trim().toLowerCase(),
    message: String(message ?? '').toString(),
  };

  if (!ALLOWED_STACK.includes(normalized.stack)) {
    throw new Error("Invalid stack. Allowed: 'backend', 'frontend'");
  }
  if (!ALLOWED_LEVEL.includes(normalized.level)) {
    throw new Error("Invalid level. Allowed: 'debug','info','warn','error','fatal'");
  }
  if (!normalized.package) {
    throw new Error("'package' is required and must be lowercase");
  }
  if (!normalized.message) {
    throw new Error("'message' is required");
  }

  return normalized;
}

async function send(endpoint, token, payload) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(endpoint, {
    method: 'POST',
    headers,
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    throw new Error('Log API request failed with status ' + res.status);
  }
  return res;
}

export function createLogger(options = {}) {
  const endpoint = options.endpoint || DEFAULT_ENDPOINT;
  const token = options.token || process.env.REACT_APP_LOG_TOKEN || undefined;
  const defaultPackage = (options.defaultPackage || '').trim().toLowerCase();

  return {
    async log(stack, level, pkg, message) {
      const payload = normalizeAndValidate({
        stack,
        level,
        pkg: pkg || defaultPackage,
        message,
      });
      return send(endpoint, token, payload);
    },
    async debug(stack, pkg, message) { return this.log(stack, 'debug', pkg, message); },
    async info(stack, pkg, message) { return this.log(stack, 'info', pkg, message); },
    async warn(stack, pkg, message) { return this.log(stack, 'warn', pkg, message); },
    async error(stack, pkg, message) { return this.log(stack, 'error', pkg, message); },
    async fatal(stack, pkg, message) { return this.log(stack, 'fatal', pkg, message); },
  };
}

export async function Log(stack, level, pkg, message, options = {}) {
  const endpoint = options.endpoint || DEFAULT_ENDPOINT;
  const token = options.token || process.env.REACT_APP_LOG_TOKEN || undefined;
  const payload = normalizeAndValidate({ stack, level, pkg, message });
  return send(endpoint, token, payload);
}

export default { createLogger, Log };
