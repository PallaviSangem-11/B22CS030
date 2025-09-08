import axios from 'axios';
import { createLogger } from './log';

const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8080';
const logger = createLogger({ defaultPackage: 'api' });
export async function createShortUrl({ url, validity, shortcode }) {
  const requestBody = {
    url,
    validity: Number.isInteger(validity) ? validity : 30,
    ...(shortcode ? { shortcode } : {}),
  };

  try {
    await logger.info('frontend', 'network', 'creating short url');
    const res = await axios.post(`${API_BASE}/shorturls`, requestBody, {
      headers: { 'Content-Type': 'application/json' },
    });
    await logger.info('frontend', 'network', 'short url created successfully');
    return res.data;
  } catch (err) {
    await logger.error('frontend', 'network', 'failed to create short url');
    throw err;
  }
}
